#
# Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS).
# All rights reserved.
# This work is provided under a BSD open-source license, see project
# license.html and license.txt
#

from __future__ import annotations

from opendis7.pdus.RadioCommunicationsFamilyPdu import RadioCommunicationsFamilyPdu
from opendis7.pdus.IntercomReferenceID import IntercomReferenceID
from opendis7.enumerations.DisPduType import DisPduType
from opendis7.enumerations.SignalTDLType import SignalTDLType


class IntercomSignalPdu(RadioCommunicationsFamilyPdu):
    """5.8.6 Conveys the audio or digital data that is used to communicate between simulated intercom devices"""

    def __init__(self):
        """Initializer for IntercomSignalPdu."""
        super().__init__()
        self.pduType = DisPduType.INTERCOM_SIGNAL
        self.intercomReferenceID = IntercomReferenceID()
        """The unique designation of an attached or unattached intercom in an event or exercise"""
        self.intercomNumber = 0
        """ID of communications device"""
        self.encodingScheme = 0
        """encoding scheme"""
        self.tdlType = SignalTDLType(0)
        """tactical data link type"""
        self.sampleRate = 0
        """sample rate"""
        self.dataLength = 0
        """data length in bits"""
        self.samples = 0
        """samples"""
        self.data = b''
        """variable-length data payload"""

    def marshalledSize(self):
        """Return the marshalled size of this object in bytes."""
        marshalSize = super().marshalledSize()
        marshalSize += self.intercomReferenceID.marshalledSize()
        marshalSize += 2  # intercomNumber (uint16)
        marshalSize += 2  # encodingScheme (uint16)
        marshalSize += 2  # tdlType (uint16 enum)
        marshalSize += 4  # sampleRate (uint32)
        marshalSize += 2  # dataLength (uint16)
        marshalSize += 2  # samples (uint16)
        byteLength = (self.dataLength + 7) // 8
        marshalSize += byteLength
        # Pad to 32-bit boundary
        remainder = marshalSize % 4
        if remainder != 0:
            marshalSize += 4 - remainder
        return marshalSize

    def serialize(self, outputStream):
        """Serialize the class to a DataOutputStream."""
        # Auto-compute dataLength if not explicitly set
        if self.dataLength == 0 and len(self.data) > 0:
            self.dataLength = len(self.data) * 8

        self.length = self.marshalledSize()
        super().serialize(outputStream)
        self.intercomReferenceID.serialize(outputStream)
        outputStream.write_unsigned_short(self.intercomNumber)
        outputStream.write_unsigned_short(self.encodingScheme)
        outputStream.write_unsigned_short(self.tdlType.value)
        outputStream.write_unsigned_int(self.sampleRate)
        outputStream.write_unsigned_short(self.dataLength)
        outputStream.write_unsigned_short(self.samples)

        byteLength = (self.dataLength + 7) // 8
        outputStream.stream.write(self.data[:byteLength])

        # Pad to 32-bit boundary
        currentPosition = outputStream.stream.tell()
        padCount = (4 - (currentPosition % 4)) % 4
        outputStream.stream.write(b'\x00' * padCount)

    def parse(self, inputStream):
        """Parse a message. This may recursively call embedded objects."""
        super().parse(inputStream)
        self.intercomReferenceID.parse(inputStream)
        self.intercomNumber = inputStream.read_unsigned_short()
        self.encodingScheme = inputStream.read_unsigned_short()
        self.tdlType = SignalTDLType(inputStream.read_unsigned_short())
        self.sampleRate = inputStream.read_unsigned_int()
        self.dataLength = inputStream.read_unsigned_short()
        self.samples = inputStream.read_unsigned_short()

        byteLength = (self.dataLength + 7) // 8
        self.data = inputStream.stream.read(byteLength)

        # Pad to 32-bit boundary
        currentPosition = inputStream.stream.tell()
        padCount = (4 - (currentPosition % 4)) % 4
        inputStream.stream.read(padCount)
