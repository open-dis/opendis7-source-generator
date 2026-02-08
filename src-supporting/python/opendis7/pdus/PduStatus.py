#
# Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS).
# All rights reserved.
# This work is provided under a BSD open-source license, see project
# license.html and license.txt
#

from __future__ import annotations


class PduStatus(object):
    """PduStatus, section 6.2.67

    Sample use:
        from opendis7.pdus.PduStatus import PduStatus
        stat = PduStatus(PduStatus.CEI_COUPLED | PduStatus.LVC_LIVE)
        stat = PduStatus()
        stat.value = PduStatus.CEI_NOT_COUPLED | PduStatus.LVC_VIRTUAL | PduStatus.TEI_NO_DIFFERENCE
    """

    # bit 0 - Transferred Entity Indication
    TEI_NO_DIFFERENCE = 0b00000000
    TEI_DIFFERENCE    = 0b00000001

    # bits 1-2 - LVC Indication
    LVC_NO_STATEMENT  = 0b00000000
    LVC_LIVE          = 0b00000010
    LVC_VIRTUAL       = 0b00000100
    LVC_CONSTRUCTIVE  = 0b00000110

    # bit 3 - Coupled Extension Indication
    CEI_NOT_COUPLED   = 0b00000000
    CEI_COUPLED       = 0b00001000

    # bit 4 - Fire Type Indication
    FTI_MUNITION      = 0b00000000
    FTI_EXPENDABLE    = 0b00010000

    # bits 4-5 - Detonation Type Indication
    DTI_MUNITION               = 0b00000000
    DTI_EXPENDABLE             = 0b00010000
    DTI_NON_MUNITION_EXPLOSION = 0b00100000

    # bits 4-5 - Radio Attached Indication
    RAI_NO_STATEMENT  = 0b00000000
    RAI_UNATTACHED    = 0b00010000
    RAI_ATTACHED      = 0b00100000

    # bits 4-5 - Intercom Attached Indication
    IAI_NO_STATEMENT  = 0b00000000
    IAI_UNATTACHED    = 0b00010000
    IAI_ATTACHED      = 0b00100000

    # bit 4 - IFF Simulation Mode
    ISM_REGENERATION  = 0b00000000
    ISM_INTERACTIVE   = 0b00010000

    # bit 5 - Active Interrogation Indication
    AII_NOT_ACTIVE    = 0b00000000
    AII_ACTIVE        = 0b00100000

    def __init__(self, value=0):
        """Initializer for PduStatus."""
        self.value = value

    def marshalledSize(self):
        """Return the marshalled size of this object in bytes."""
        return 1

    def serialize(self, outputStream):
        """Serialize the class to a DataOutputStream."""
        outputStream.write_unsigned_byte(self.value)

    def parse(self, inputStream):
        """Parse a message. This may recursively call embedded objects."""
        self.value = inputStream.read_unsigned_byte()

    def __eq__(self, other):
        if isinstance(other, PduStatus):
            return self.value == other.value
        return NotImplemented

    def __repr__(self):
        return "PduStatus: " + format(self.value, '08b')
