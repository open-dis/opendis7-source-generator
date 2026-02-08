#
# Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS).
# All rights reserved.
# This work is provided under a BSD open-source license, see project
# license.html and license.txt
#

from __future__ import annotations


class Domain(object):
    """Which domain does this PDU belong to. Wraps domain enum values
    (PlatformDomain, MunitionDomain, SupplyDomain) with a unified interface."""

    def __init__(self):
        """Initializer for Domain."""
        self._enum_value = 0
        self._description = ""

    @classmethod
    def inst(cls, enum_or_int):
        """Factory method to create a Domain from an enum or int value.

        Args:
            enum_or_int: A PlatformDomain, MunitionDomain, SupplyDomain enum
                         or an integer value.

        Returns:
            A Domain instance wrapping the given value.
        """
        d = cls()
        if hasattr(enum_or_int, 'value'):
            d._enum_value = enum_or_int.value
            if hasattr(enum_or_int, 'name'):
                d._description = enum_or_int.name
        else:
            d._enum_value = int(enum_or_int)
            d._description = str(enum_or_int)
        return d

    @property
    def value(self):
        return self._enum_value

    @property
    def description(self):
        return self._description

    def marshalledSize(self):
        """Return the marshalled size of this object in bytes."""
        return 1

    def serialize(self, outputStream):
        """Serialize the class to a DataOutputStream."""
        outputStream.write_unsigned_byte(self._enum_value)

    def parse(self, inputStream):
        """Parse a message. This may recursively call embedded objects."""
        self._enum_value = inputStream.read_unsigned_byte()

    def __eq__(self, other):
        if isinstance(other, Domain):
            return self._enum_value == other._enum_value
        return NotImplemented

    def __repr__(self):
        return "Domain: " + self._description + " (" + str(self._enum_value) + ")"
