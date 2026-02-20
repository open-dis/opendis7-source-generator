#
# Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS).
# All rights reserved.
# This work is provided under a BSD open-source license, see project
# license.html and license.txt
#

from __future__ import annotations

from opendis7.enumerations.VariableRecordType import VariableRecordType


class VariableDatum(object):
    """The variable datum type, the datum length, and the value for that variable datum type. Section 6.2.93"""

    def __init__(self):
        """Initializer for VariableDatum."""
        self.variableDatumID = VariableRecordType(0)
        """Type of variable datum to be transmitted. 32-bit enumeration defined in EBV"""
        self.variableDatumLength = 0
        """Length, IN BITS, of the variable datum."""
        self.variableDatumValue = b''
        """Variable-length datum value as raw bytes."""

    def marshalledSize(self):
        """Return the marshalled size of this object in bytes."""
        marshalSize = 0
        marshalSize += 4  # variableDatumID (uint32 enum)
        marshalSize += 4  # variableDatumLength (uint32)
        byteLength = (self.variableDatumLength + 7) // 8
        marshalSize += byteLength
        # Pad to 64-bit boundary
        remainder = marshalSize % 8
        if remainder != 0:
            marshalSize += 8 - remainder
        return marshalSize

    def serialize(self, outputStream):
        """Serialize the class to a DataOutputStream."""
        # Auto-compute variableDatumLength if not explicitly set
        if self.variableDatumLength == 0 and len(self.variableDatumValue) > 0:
            self.variableDatumLength = len(self.variableDatumValue) * 8

        outputStream.write_unsigned_int(self.variableDatumID.value)
        outputStream.write_unsigned_int(self.variableDatumLength)

        byteLength = (self.variableDatumLength + 7) // 8
        outputStream.stream.write(self.variableDatumValue[:byteLength])

        # Pad to 64-bit boundary (8 bytes)
        # Total so far: 4 (ID) + 4 (length) + byteLength = 8 + byteLength
        currentPosition = outputStream.stream.tell()
        padCount = (8 - (currentPosition % 8)) % 8
        outputStream.stream.write(b'\x00' * padCount)

    def parse(self, inputStream):
        """Parse a message. This may recursively call embedded objects."""
        self.variableDatumID = VariableRecordType(inputStream.read_unsigned_int())
        self.variableDatumLength = inputStream.read_unsigned_int()

        byteLength = (self.variableDatumLength + 7) // 8
        self.variableDatumValue = inputStream.stream.read(byteLength)

        # Pad to 64-bit boundary (8 bytes)
        currentPosition = inputStream.stream.tell()
        padCount = (8 - (currentPosition % 8)) % 8
        inputStream.stream.read(padCount)
