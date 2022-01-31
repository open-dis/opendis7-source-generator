#
#This code is licensed under the BSD software license
# Copyright 2009-2022, MOVES Institute
# Author: DMcG
#

import DataInputStream
import DataOutputStream


class StartResumeReliablePdu( object ):
    """alias, more descriptive name for a StartResumeRPdu."""

    def __init__(self):
        """ Initializer for StartResumeReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class EmitterBeam( object ):
    """null"""

    def __init__(self):
        """ Initializer for EmitterBeam"""
        self.beamDataLength = 0
        self.beamNumber = 0
        self.beamParameterIndex = 0
        self.fundamentalParameterData = EEFundamentalParameterData();
        self.beamData = BeamData();
        self.numberOfTargets = 0
        self.beamStatus = BeamStatus();
        self.jammingTechnique = JammingTechnique();
        self.trackJamData = []

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.beamDataLength);
        outputStream.write_null(self.beamNumber);
        outputStream.write_null(self.beamParameterIndex);
        self.fundamentalParameterData.serialize(outputStream)
        self.beamData.serialize(outputStream)
        outputStream.write_null( len(self.trackJamData));
        self.beamStatus.serialize(outputStream)
        self.jammingTechnique.serialize(outputStream)
        for anObj in self.trackJamData:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.beamDataLength = inputStream.read_null();
        self.beamNumber = inputStream.read_null();
        self.beamParameterIndex = inputStream.read_null();
        self.fundamentalParameterData.parse(inputStream)
        self.beamData.parse(inputStream)
        self.numberOfTargets = inputStream.read_null();
        self.beamStatus.parse(inputStream)
        self.jammingTechnique.parse(inputStream)
        for idx in range(0, self.numberOfTargets):
            element = null()
            element.parse(inputStream)
            self.trackJamData.append(element)




class MunitionDescriptor( object ):
    """Represents the firing or detonation of a munition. Section 6.2.19.2"""

    def __init__(self):
        """ Initializer for MunitionDescriptor"""
        self.munitionType = EntityType();
        """ What munition was used in the burst"""
        self.quantity = 0
        """ how many of the munition were fired"""
        self.rate = 0
        """ rate at which the munition was fired"""

    def serialize(self, outputStream):
        """serialize the class """
        self.munitionType.serialize(outputStream)
        outputStream.write_null(self.quantity);
        outputStream.write_null(self.rate);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.munitionType.parse(inputStream)
        self.quantity = inputStream.read_null();
        self.rate = inputStream.read_null();



class DataQueryReliablePdu( object ):
    """alias, more descriptive name for a DataQueryRPdu."""

    def __init__(self):
        """ Initializer for DataQueryReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class MinefieldSensorType( object ):
    """Information about a minefield sensor. Section 6.2.57"""

    def __init__(self):
        """ Initializer for MinefieldSensorType"""
        self.sensorType = 0
        """ sensor type. bit fields 0-3 are the type category, 4-15 are teh subcategory"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.sensorType);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.sensorType = inputStream.read_null();



class LayerHeader( object ):
    """The identification of the additional information layer number, layer-specific information, and the length of the layer. Section 6.2.51"""

    def __init__(self):
        """ Initializer for LayerHeader"""
        self.layerNumber = 0
        self.layerSpecificInformation = 0
        """ field shall specify layer-specific information that varies by System Type (see 6.2.86) and Layer Number."""
        self.length = 0
        """ This field shall specify the length in octets of the layer, including the Layer Header record"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.layerNumber);
        outputStream.write_null(self.layerSpecificInformation);
        outputStream.write_null(self.length);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.layerNumber = inputStream.read_null();
        self.layerSpecificInformation = inputStream.read_null();
        self.length = inputStream.read_null();



class AngleDeception( object ):
    """The Angle Deception attribute record may be used to communicate discrete values that are associated with angle deception jamming that cannot be referenced to an emitter mode. The values provided in the record records (provided in the associated Electromagnetic Emission PDU). (The victim radar beams are those that are targeted by the jammer.) Section 6.2.21.2.2"""

    def __init__(self):
        """ Initializer for AngleDeception"""
        self.recordType = 3501
        """ record type"""
        self.recordLength = 48
        """ The length of the record in octets."""
        self.padding = 0
        """ padding"""
        self.emitterNumber = 0
        """ indicates the emitter system for which the angle deception values are applicable. """
        self.beamNumber = 0
        """ indicates the jamming beam for which the angle deception values are applicable."""
        self.padding2 = 0
        """ padding"""
        self.azimuthOffset = 0
        """ This field indicates the relative azimuth angle at which the deceptive radar returns are centered. This angle is measured in the X-Y plane of the victim radar's entity coordinate system (see 1.4.3). This angle is measured in radians from the victim radar entity's azimuth for the true jam- mer position to the center of the range of azimuths in which deceptive radar returns are perceived as shown in Figure 43. Positive and negative values indicate that the perceived positions of the jammer are right and left of the true position of the jammer, respectively. The range of permissible values is -PI to PI"""
        self.azimuthWidth = 0
        """ indicates the range of azimuths (in radians) through which the deceptive radar returns are perceived, centered on the azimuth offset as shown in Figure 43. The range of permissible values is 0 to 2PI radians"""
        self.azimuthPullRate = 0
        """ This field indicates the rate (in radians per second) at which the Azimuth Offset value is changing. Positive and negative values indicate that the Azimuth Offset is moving to the right or left, respectively."""
        self.azimuthPullAcceleration = 0
        """ This field indicates the rate (in radians per second squared) at which the Azimuth Pull Rate value is changing. Azimuth Pull Acceleration is defined as positive to the right and negative to the left."""
        self.elevationOffset = 0
        """ This field indicates the relative elevation angle at which the deceptive radar returns begin. This angle is measured as an angle with respect to the X-Y plane of the victim radar's entity coordinate system (see 1.4.3). This angle is measured in radians from the victim radar entity's eleva- tion for the true jammer position to the center of the range of elevations in which deceptive radar returns are perceived as shown in Figure 44. Positive and negative values indicate that the perceived positions of the jammer are above and below the true position of the jammer, respectively. The range of permissible values is -PI/2 to PI/2"""
        self.elevationWidth = 0
        """ This field indicates the range of elevations (in radians) through which the decep- tive radar returns are perceived, centered on the elevation offset as shown in Figure 44. The range of permissible values is 0 to PI radians"""
        self.elevationPullRate = 0
        """ This field indicates the rate (in radians per second) at which the Elevation Off- set value is changing. Positive and negative values indicate that the Elevation Offset is moving up or down, respectively. """
        self.elevationPullAcceleration = 0
        """ This field indicates the rate (in radians per second squared) at which the Elevation Pull Rate value is changing. Elevation Pull Acceleration is defined as positive to upward and negative downward. """
        self.padding3 = 0
        """ """

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        outputStream.write_null(self.emitterNumber);
        outputStream.write_null(self.beamNumber);
        outputStream.write_null(self.padding2);
        outputStream.write_null(self.azimuthOffset);
        outputStream.write_null(self.azimuthWidth);
        outputStream.write_null(self.azimuthPullRate);
        outputStream.write_null(self.azimuthPullAcceleration);
        outputStream.write_null(self.elevationOffset);
        outputStream.write_null(self.elevationWidth);
        outputStream.write_null(self.elevationPullRate);
        outputStream.write_null(self.elevationPullAcceleration);
        outputStream.write_null(self.padding3);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.emitterNumber = inputStream.read_null();
        self.beamNumber = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.azimuthOffset = inputStream.read_null();
        self.azimuthWidth = inputStream.read_null();
        self.azimuthPullRate = inputStream.read_null();
        self.azimuthPullAcceleration = inputStream.read_null();
        self.elevationOffset = inputStream.read_null();
        self.elevationWidth = inputStream.read_null();
        self.elevationPullRate = inputStream.read_null();
        self.elevationPullAcceleration = inputStream.read_null();
        self.padding3 = inputStream.read_null();



class VectoringNozzleSystem( object ):
    """Operational data for describing the vectoring nozzle systems Section 6.2.96"""

    def __init__(self):
        """ Initializer for VectoringNozzleSystem"""
        self.horizontalDeflectionAngle = 0
        """ In degrees"""
        self.verticalDeflectionAngle = 0
        """ In degrees"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.horizontalDeflectionAngle);
        outputStream.write_null(self.verticalDeflectionAngle);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.horizontalDeflectionAngle = inputStream.read_null();
        self.verticalDeflectionAngle = inputStream.read_null();



class DataReliablePdu( object ):
    """alias, more descriptive name for a DataRPdu."""

    def __init__(self):
        """ Initializer for DataReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class RadioType( object ):
    """Identifies the type of radio. Section 6.2.71"""

    def __init__(self):
        """ Initializer for RadioType"""
        self.domain = 0
        """ Domain of entity (air, surface, subsurface, space, etc.)"""
        self.specific = 0
        self.extra = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.domain);
        outputStream.write_null(self.specific);
        outputStream.write_null(self.extra);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.domain = inputStream.read_null();
        self.specific = inputStream.read_null();
        self.extra = inputStream.read_null();



class IdentificationFriendOrFoePdu( object ):
    """alias, more descriptive name for an IFFPdu."""

    def __init__(self):
        """ Initializer for IdentificationFriendOrFoePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class BeamAntennaPattern( object ):
    """Used when the antenna pattern type field has a value of 1. Specifies the direction, pattern, and polarization of radiation from an antenna. Section 6.2.9.2"""

    def __init__(self):
        """ Initializer for BeamAntennaPattern"""
        self.beamDirection = EulerAngles();
        """ The rotation that transforms the reference coordinate sytem into the beam coordinate system. Either world coordinates or entity coordinates may be used as the reference coordinate system, as specified by the reference system field of the antenna pattern record."""
        self.azimuthBeamwidth = 0
        """ Full width of the beam to the -3dB power density points in the x-y plane of the beam coordinnate system.  Elevation beamwidth is represented by a 32-bit floating point number in units of radians."""
        self.elevationBeamwidth = 0
        """ This field shall specify the full width of the beam to the –3 dB power density points in the x-z plane of the beam coordinate system. Elevation beamwidth shall be represented by a 32-bit floating point number in units of radians."""
        self.padding1 = 0
        """ Padding"""
        self.padding2 = 0
        """ Padding"""
        self.ez = 0.0
        """ This field shall specify the magnitude of the Z-component (in beam coordinates) of the Electrical field at some arbitrary single point in the main beam and in the far field of the antenna. """
        self.ex = 0.0
        """ This field shall specify the magnitude of the X-component (in beam coordinates) of the Electri- cal field at some arbitrary single point in the main beam and in the far field of the antenna."""
        self.phase = 0.0
        """ This field shall specify the phase angle between EZ and EX in radians. If fully omni-direc- tional antenna is modeled using beam pattern type one, the omni-directional antenna shall be repre- sented by beam direction Euler angles psi, theta, and phi of zero, an azimuth beamwidth of 2PI, and an elevation beamwidth of PI"""
        self.padding3 = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        self.beamDirection.serialize(outputStream)
        outputStream.write_null(self.azimuthBeamwidth);
        outputStream.write_null(self.elevationBeamwidth);
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        outputStream.write_null(self.ez);
        outputStream.write_null(self.ex);
        outputStream.write_null(self.phase);
        outputStream.write_null(self.padding3);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.beamDirection.parse(inputStream)
        self.azimuthBeamwidth = inputStream.read_null();
        self.elevationBeamwidth = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.ez = inputStream.read_null();
        self.ex = inputStream.read_null();
        self.phase = inputStream.read_null();
        self.padding3 = inputStream.read_null();



class ElectronicEmitter( object ):
    """null"""

    def __init__(self):
        """ Initializer for ElectronicEmitter"""
        self.systemDataLength = 0
        """  this field shall specify the length of this emitter system's data in 32-bit words."""
        self.numberOfBeams = 0
        """ the number of beams being described in the current PDU for the emitter system being described. """
        self.emitterSystem = EmitterSystem();
        """  information about a particular emitter system and shall be represented by an Emitter System record (see 6.2.23)."""
        self.location = Vector3Float();
        """ the location of the antenna beam source with respect to the emitting entity's coordinate system. This location shall be the origin of the emitter coordinate system that shall have the same orientation as the entity coordinate system. This field shall be represented by an Entity Coordinate Vector record see 6.2.95 """
        self.beams = []
        """ Electronic emmission beams"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.systemDataLength);
        outputStream.write_null( len(self.beams));
        self.emitterSystem.serialize(outputStream)
        self.location.serialize(outputStream)
        for anObj in self.beams:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.systemDataLength = inputStream.read_null();
        self.numberOfBeams = inputStream.read_null();
        self.emitterSystem.parse(inputStream)
        self.location.parse(inputStream)
        for idx in range(0, self.numberOfBeams):
            element = null()
            element.parse(inputStream)
            self.beams.append(element)




class VariableTransmitterParameters( object ):
    """Relates to radios. Section 6.2.95"""

    def __init__(self):
        """ Initializer for VariableTransmitterParameters"""
        self.recordLength = 0
        """ Length, in bytes"""
        self.recordSpecificFields =  []

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordLength);
        for idx in range(0, 0):
            outputStream.write_null( self.recordSpecificFields[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordLength = inputStream.read_null();
        self.recordSpecificFields = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordSpecificFields[  idx  ] = val




class Attribute( object ):
    """Used to convey information for one or more attributes. Attributes conform to the standard variable record format of 6.2.82. Section 6.2.10."""

    def __init__(self):
        """ Initializer for Attribute"""
        self.recordType = 0
        """ The record type for this attribute. Enumeration"""
        self.recordLength = 0
        """ Total length of the record in octets, including padding. The record shall end on a 64-bit boundary after any padding. = 6 + K + P"""
        self.recordSpecificFields =  []
        """ The attribute data format conforming to that specified by the record type. K bytes long"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        for idx in range(0, 0):
            outputStream.write_null( self.recordSpecificFields[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.recordSpecificFields = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordSpecificFields[  idx  ] = val




class AttributeRecordSet( object ):
    """null"""

    def __init__(self):
        """ Initializer for AttributeRecordSet"""
        self.entityId = EntityID();
        self.numberOfAttributeRecords = 0
        self.attributeRecords = []

    def serialize(self, outputStream):
        """serialize the class """
        self.entityId.serialize(outputStream)
        outputStream.write_null( len(self.attributeRecords));
        for anObj in self.attributeRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityId.parse(inputStream)
        self.numberOfAttributeRecords = inputStream.read_null();
        for idx in range(0, self.numberOfAttributeRecords):
            element = null()
            element.parse(inputStream)
            self.attributeRecords.append(element)




class Association( object ):
    """An entity's associations with other entities and/or locations. For each association, this record shall specify the type of the association, the associated entity's EntityID and/or the associated location's world coordinates. This record may be used (optionally) in a transfer transaction to send internal state data from the divesting simulation to the acquiring simulation (see 5.9.4). This record may also be used for other purposes. Section 6.2.9"""

    def __init__(self):
        """ Initializer for Association"""
        self.padding = 0
        """ Padding"""
        self.associatedEntityID = EntityIdentifier();
        """ identity of associated entity. If none, NO_SPECIFIC_ENTITY"""
        self.associatedLocation = Vector3Double();
        """ location, in world coordinates"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.padding);
        self.associatedEntityID.serialize(outputStream)
        self.associatedLocation.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.padding = inputStream.read_null();
        self.associatedEntityID.parse(inputStream)
        self.associatedLocation.parse(inputStream)



class LiveEntityOrientationError( object ):
    """16-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveEntityOrientationError"""
        self.azimuthError = 0
        self.elevationError = 0
        self.rotationError = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.azimuthError);
        outputStream.write_null(self.elevationError);
        outputStream.write_null(self.rotationError);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.azimuthError = inputStream.read_null();
        self.elevationError = inputStream.read_null();
        self.rotationError = inputStream.read_null();



class RecordSpecificationElement( object ):
    """Synthetic record, made up from section 6.2.73. This is used to achieve a repeating variable list element.<p>recordLength, recordCount and recordValues must be set by hand so the."""

    def __init__(self):
        """ Initializer for RecordSpecificationElement"""
        self.recordSetSerialNumber = 0
        """ The serial number of the first record in the block of records"""
        self.padding = 0
        self.recordLength = 0
        """  the length, in bits, of the record. Note, bits, not bytes."""
        self.recordCount = 0
        """  the number of records included in the record set """
        self.recordValues =  []
        """ The concatenated records of the format specified by the Record ID field. The length of this field is the Record Length multiplied by the Record Count, in units of bits."""
        self.padTo64 =  []
        """ used if required to make entire record size an even multiple of 8 bytes"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordSetSerialNumber);
        outputStream.write_null(self.padding);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.recordCount);
        for idx in range(0, 0):
            outputStream.write_null( self.recordValues[ idx ] );

        for idx in range(0, 0):
            outputStream.write_null( self.padTo64[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordSetSerialNumber = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.recordCount = inputStream.read_null();
        self.recordValues = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordValues[  idx  ] = val

        self.padTo64 = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.padTo64[  idx  ] = val




class AntennaLocation( object ):
    """Location of the radiating portion of the antenna, specified in world coordinates and entity coordinates. Section 6.2.8"""

    def __init__(self):
        """ Initializer for AntennaLocation"""
        self.antennaLocation = Vector3Double();
        """ Location of the radiating portion of the antenna in world coordinates"""
        self.relativeAntennaLocation = Vector3Float();
        """ Location of the radiating portion of the antenna in entity coordinates"""

    def serialize(self, outputStream):
        """serialize the class """
        self.antennaLocation.serialize(outputStream)
        self.relativeAntennaLocation.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.antennaLocation.parse(inputStream)
        self.relativeAntennaLocation.parse(inputStream)



class ObjectIdentifier( object ):
    """The unique designation of an environmental object. Section 6.2.63"""

    def __init__(self):
        """ Initializer for ObjectIdentifier"""
        self.simulationAddress = SimulationAddress();
        """  Simulation Address"""
        self.objectNumber = 0
        """ object number"""

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.objectNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.objectNumber = inputStream.read_null();



class FixedDatum( object ):
    """Fixed Datum Record. Section 6.2.37"""

    def __init__(self):
        """ Initializer for FixedDatum"""
        self.fixedDatumValue = 0
        """ Value for the fixed datum"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.fixedDatumValue);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.fixedDatumValue = inputStream.read_null();



class VariableParameter( object ):
    """used in DetonationPdu, ArticulatedPartsPdu among others"""

    def __init__(self):
        """ Initializer for VariableParameter"""
        self.recordSpecificFields =  [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        """ 120 bits"""

    def serialize(self, outputStream):
        """serialize the class """
        for idx in range(0, 15):
            outputStream.write_null( self.recordSpecificFields[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordSpecificFields = [0]*15
        for idx in range(0, 15):
            val = inputStream.read_null
            self.recordSpecificFields[  idx  ] = val




class ChangeOptions( object ):
    """This is a bitfield. See section 6.2.13 aka B.2.41"""

    def __init__(self):
        """ Initializer for ChangeOptions"""
        self.value = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.value);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.value = inputStream.read_null();



class LiveSimulationAddress( object ):
    """A simulation's designation associated with all Live Entity IDs contained in Live Entity PDUs. Section 6.2.55 """

    def __init__(self):
        """ Initializer for LiveSimulationAddress"""
        self.liveSiteNumber = 0
        """ facility, installation, organizational unit or geographic location may have multiple sites associated with it. The Site Number is the first component of the Live Simulation Address, which defines a live simulation."""
        self.liveApplicationNumber = 0
        """ An application associated with a live site is termed a live application. Each live application participating in an event """

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.liveSiteNumber);
        outputStream.write_null(self.liveApplicationNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.liveSiteNumber = inputStream.read_null();
        self.liveApplicationNumber = inputStream.read_null();



class UAFundamentalParameter( object ):
    """Regeneration parameters for active emission systems that are variable throughout a scenario. Section 6.2.91"""

    def __init__(self):
        """ Initializer for UAFundamentalParameter"""
        self.beamCenterAzimuthHorizontal = 0
        """ center azimuth bearing of th emain beam. In radians."""
        self.azimuthalBeamwidthHorizontal = 0
        """ Horizontal beamwidth of th emain beam Meastued at the 3dB down point of peak radiated power. In radians."""
        self.beamCenterDepressionElevation = 0
        """ center of the d/e angle of th emain beam relative to the stablised de angle of the target. In radians."""
        self.depressionElevationBeamWidth = 0
        """ vertical beamwidth of the main beam. Meastured at the 3dB down point of peak radiated power. In radians."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.beamCenterAzimuthHorizontal);
        outputStream.write_null(self.azimuthalBeamwidthHorizontal);
        outputStream.write_null(self.beamCenterDepressionElevation);
        outputStream.write_null(self.depressionElevationBeamWidth);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.beamCenterAzimuthHorizontal = inputStream.read_null();
        self.azimuthalBeamwidthHorizontal = inputStream.read_null();
        self.beamCenterDepressionElevation = inputStream.read_null();
        self.depressionElevationBeamWidth = inputStream.read_null();



class DirectedEnergyDamage( object ):
    """Damage sustained by an entity due to directed energy. Location of the damage based on a relative x,y,z location from the center of the entity. Section 6.2.15.2"""

    def __init__(self):
        """ Initializer for DirectedEnergyDamage"""
        self.recordType = 4500
        """ DE Record Type."""
        self.recordLength = 40
        """ DE Record Length (bytes)"""
        self.padding = 0
        """ padding"""
        self.damageLocation = Vector3Float();
        """ location of damage, relative to center of entity"""
        self.damageDiameter = 0
        """ Size of damaged area, in meters"""
        self.temperature = -273.15
        """ average temp of the damaged area, in degrees celsius. If firing entitty does not model this, use a value of -273.15"""
        self.fireEventID = EventIdentifier();
        """ For any component damage resulting this field shall be set to the fire event ID from that PDU."""
        self.padding2 = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        self.damageLocation.serialize(outputStream)
        outputStream.write_null(self.damageDiameter);
        outputStream.write_null(self.temperature);
        self.fireEventID.serialize(outputStream)
        outputStream.write_null(self.padding2);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.damageLocation.parse(inputStream)
        self.damageDiameter = inputStream.read_null();
        self.temperature = inputStream.read_null();
        self.fireEventID.parse(inputStream)
        self.padding2 = inputStream.read_null();



class ExplosionDescriptor( object ):
    """Explosion of a non-munition. Section 6.2.19.3"""

    def __init__(self):
        """ Initializer for ExplosionDescriptor"""
        self.explodingObject = EntityType();
        """ Type of the object that exploded. See 6.2.30"""
        self.padding = 0
        """ padding"""
        self.explosiveForce = 0
        """ Force of explosion, in equivalent KG of TNT"""

    def serialize(self, outputStream):
        """serialize the class """
        self.explodingObject.serialize(outputStream)
        outputStream.write_null(self.padding);
        outputStream.write_null(self.explosiveForce);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.explodingObject.parse(inputStream)
        self.padding = inputStream.read_null();
        self.explosiveForce = inputStream.read_null();



class ClockTime( object ):
    """Time measurements that exceed one hour are represented by this record. The first field is the hours since the unix epoch (Jan 1 1970, used by most Unix systems and java) and the second field the timestamp units since the top of the hour. Section 6.2.14"""

    def __init__(self):
        """ Initializer for ClockTime"""
        self.hour = 0
        """ Hours in UTC"""
        self.timePastHour = 0
        """ Time past the hour"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.hour);
        outputStream.write_null(self.timePastHour);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.hour = inputStream.read_null();
        self.timePastHour = inputStream.read_null();



class SecondaryOperationalData( object ):
    """Additional operational data for an IFF emitting system and the number of IFF Fundamental Parameter Data records Section 6.2.76."""

    def __init__(self):
        """ Initializer for SecondaryOperationalData"""
        self.operationalData1 = 0
        """ additional operational characteristics of the IFF emitting system. Each 8-bit field will vary depending on the system type."""
        self.operationalData2 = 0
        """ additional operational characteristics of the IFF emitting system. Each 8-bit field will vary depending on the system type."""
        self.numberOfIFFFundamentalParameterRecords = 0
        """ The number of IFF Fundamental Parameter Data records that follow"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.operationalData1);
        outputStream.write_null(self.operationalData2);
        outputStream.write_null(self.numberOfIFFFundamentalParameterRecords);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.operationalData1 = inputStream.read_null();
        self.operationalData2 = inputStream.read_null();
        self.numberOfIFFFundamentalParameterRecords = inputStream.read_null();



class RemoveEntityReliablePdu( object ):
    """alias, more descriptive name for a RemoveEntityRPdu."""

    def __init__(self):
        """ Initializer for RemoveEntityReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class Relationship( object ):
    """The relationship of the part entity to its host entity. Section 6.2.74."""

    def __init__(self):
        """ Initializer for Relationship"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class EEFundamentalParameterData( object ):
    """Contains electromagnetic emmission regeneration parameters that are variable throught a scenario. Section 6.2.22."""

    def __init__(self):
        """ Initializer for EEFundamentalParameterData"""
        self.frequency = 0
        """ center frequency of the emission in hertz."""
        self.frequencyRange = 0
        """ Bandwidth of the frequencies corresponding to the fequency field."""
        self.effectiveRadiatedPower = 0
        """ Effective radiated power for the emission in DdBm. For a radar noise jammer, indicates the peak of the transmitted power."""
        self.pulseRepetitionFrequency = 0
        """ Average repetition frequency of the emission in hertz."""
        self.pulseWidth = 0
        """ Average pulse width  of the emission in microseconds."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.frequency);
        outputStream.write_null(self.frequencyRange);
        outputStream.write_null(self.effectiveRadiatedPower);
        outputStream.write_null(self.pulseRepetitionFrequency);
        outputStream.write_null(self.pulseWidth);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.frequency = inputStream.read_null();
        self.frequencyRange = inputStream.read_null();
        self.effectiveRadiatedPower = inputStream.read_null();
        self.pulseRepetitionFrequency = inputStream.read_null();
        self.pulseWidth = inputStream.read_null();



class RadioCommsHeader( object ):
    """null"""

    def __init__(self):
        """ Initializer for RadioCommsHeader"""
        self.radioReferenceID = EntityID();
        """ ID of the entitythat is the source of the communication"""
        self.radioNumber = 0
        """ particular radio within an entity"""

    def serialize(self, outputStream):
        """serialize the class """
        self.radioReferenceID.serialize(outputStream)
        outputStream.write_null(self.radioNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.radioReferenceID.parse(inputStream)
        self.radioNumber = inputStream.read_null();



class LEVector3FixedByte( object ):
    """3 x 8-bit fixed binary"""

    def __init__(self):
        """ Initializer for LEVector3FixedByte"""
        self.x = 0
        """ X value"""
        self.y = 0
        """ y Value"""
        self.z = 0
        """ Z value"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.x);
        outputStream.write_null(self.y);
        outputStream.write_null(self.z);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.x = inputStream.read_null();
        self.y = inputStream.read_null();
        self.z = inputStream.read_null();



class DirectedEnergyAreaAimpoint( object ):
    """DE Precision Aimpoint Record. Section 6.2.20.2"""

    def __init__(self):
        """ Initializer for DirectedEnergyAreaAimpoint"""
        self.recordType = 4001
        """ Type of Record enumeration"""
        self.recordLength = 0
        """ Length of Record"""
        self.padding = 0
        """ Padding"""
        self.beamAntennaPatternRecordCount = 0
        """ Number of beam antenna pattern records"""
        self.directedEnergyTargetEnergyDepositionRecordCount = 0
        """ Number of DE target energy depositon records"""
        self.beamAntennaParameterList = []
        """ list of beam antenna records. See 6.2.9.2"""
        self.directedEnergyTargetEnergyDepositionRecordList = []
        """ list of DE target deposition records. See 6.2.21.4"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        outputStream.write_null( len(self.beamAntennaParameterList));
        outputStream.write_null( len(self.directedEnergyTargetEnergyDepositionRecordList));
        for anObj in self.beamAntennaParameterList:
            anObj.serialize(outputStream)

        for anObj in self.directedEnergyTargetEnergyDepositionRecordList:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.beamAntennaPatternRecordCount = inputStream.read_null();
        self.directedEnergyTargetEnergyDepositionRecordCount = inputStream.read_null();
        for idx in range(0, self.beamAntennaPatternRecordCount):
            element = null()
            element.parse(inputStream)
            self.beamAntennaParameterList.append(element)

        for idx in range(0, self.directedEnergyTargetEnergyDepositionRecordCount):
            element = null()
            element.parse(inputStream)
            self.directedEnergyTargetEnergyDepositionRecordList.append(element)




class Vector3Float( object ):
    """Three floating point values, x, y, and z. Section 6.2.95"""

    def __init__(self):
        """ Initializer for Vector3Float"""
        self.x = 0
        """ X value"""
        self.y = 0
        """ y Value"""
        self.z = 0
        """ Z value"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.x);
        outputStream.write_null(self.y);
        outputStream.write_null(self.z);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.x = inputStream.read_null();
        self.y = inputStream.read_null();
        self.z = inputStream.read_null();



class Expendable( object ):
    """An entity's expendable (chaff, flares, etc.) information. Section 6.2.36"""

    def __init__(self):
        """ Initializer for Expendable"""
        self.expendable = EntityType();
        """ Type of expendable"""
        self.station = 0
        self.quantity = 0
        self.padding = 0

    def serialize(self, outputStream):
        """serialize the class """
        self.expendable.serialize(outputStream)
        outputStream.write_null(self.station);
        outputStream.write_null(self.quantity);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.expendable.parse(inputStream)
        self.station = inputStream.read_null();
        self.quantity = inputStream.read_null();
        self.padding = inputStream.read_null();



class LinearSegmentParameter( object ):
    """The specification of an individual segment of a linear segment synthetic environment object in a Linear Object State PDU Section 6.2.52"""

    def __init__(self):
        """ Initializer for LinearSegmentParameter"""
        self.segmentNumber = 0
        """ The individual segment of the linear segment"""
        self.specificSegmentAppearance = 0
        """ This field shall specify specific dynamic appearance attributes of the segment. This record shall be defined as a 32-bit record of enumerations."""
        self.segmentLocation = Vector3Double();
        """ This field shall specify the location of the linear segment in the simulated world and shall be represented by a World Coordinates record """
        self.segmentOrientation = EulerAngles();
        """ orientation of the linear segment about the segment location and shall be represented by a Euler Angles record """
        self.segmentLength = 0
        """ length of the linear segment, in meters, extending in the positive X direction"""
        self.segmentWidth = 0
        """ The total width of the linear segment, in meters, shall be specified by a 16-bit unsigned integer. One-half of the width shall extend in the positive Y direction, and one-half of the width shall extend in the negative Y direction."""
        self.segmentHeight = 0
        """ The height of the linear segment, in meters, above ground shall be specified by a 16-bit unsigned integer."""
        self.segmentDepth = 0
        """ The depth of the linear segment, in meters, below ground level """
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.segmentNumber);
        outputStream.write_null(self.specificSegmentAppearance);
        self.segmentLocation.serialize(outputStream)
        self.segmentOrientation.serialize(outputStream)
        outputStream.write_null(self.segmentLength);
        outputStream.write_null(self.segmentWidth);
        outputStream.write_null(self.segmentHeight);
        outputStream.write_null(self.segmentDepth);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.segmentNumber = inputStream.read_null();
        self.specificSegmentAppearance = inputStream.read_null();
        self.segmentLocation.parse(inputStream)
        self.segmentOrientation.parse(inputStream)
        self.segmentLength = inputStream.read_null();
        self.segmentWidth = inputStream.read_null();
        self.segmentHeight = inputStream.read_null();
        self.segmentDepth = inputStream.read_null();
        self.padding = inputStream.read_null();



class SimulationAddress( object ):
    """A Simulation Address record shall consist of the Site Identification number and the Application Identification number. Section 6.2.79 """

    def __init__(self):
        """ Initializer for SimulationAddress"""
        self.site = 0
        """ A site is defined as a facility, installation, organizational unit or a geographic location that has one or more simulation applications capable of participating in a distributed event. """
        self.application = 0
        """ An application is defined as a software program that is used to generate and process distributed simulation data including live, virtual and constructive data."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.site);
        outputStream.write_null(self.application);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.site = inputStream.read_null();
        self.application = inputStream.read_null();



class TrackJamData( object ):
    """ Track-Jam data Section 6.2.89"""

    def __init__(self):
        """ Initializer for TrackJamData"""
        self.entityID = EntityID();
        """ The entity tracked or illumated, or an emitter beam targeted with jamming"""
        self.emitterNumber = 0
        """ Emitter system associated with the entity"""
        self.beamNumber = 0
        """ Beam associated with the entity"""

    def serialize(self, outputStream):
        """serialize the class """
        self.entityID.serialize(outputStream)
        outputStream.write_null(self.emitterNumber);
        outputStream.write_null(self.beamNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityID.parse(inputStream)
        self.emitterNumber = inputStream.read_null();
        self.beamNumber = inputStream.read_null();



class AggregateType( object ):
    """Identifies the type and organization of an aggregate. Section 6.2.5"""

    def __init__(self):
        """ Initializer for AggregateType"""
        self.category = 0
        """ category of entity"""
        self.extra = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.category);
        outputStream.write_null(self.extra);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.category = inputStream.read_null();
        self.extra = inputStream.read_null();



class LiveEntityFirePdu( object ):
    """alias, more descriptive name for a LEFirePdu."""

    def __init__(self):
        """ Initializer for LiveEntityFirePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class BeamData( object ):
    """Describes the scan volue of an emitter beam. Section 6.2.11."""

    def __init__(self):
        """ Initializer for BeamData"""
        self.beamAzimuthCenter = 0
        """ Specifies the beam azimuth an elevation centers and corresponding half-angles to describe the scan volume"""
        self.beamAzimuthSweep = 0
        """ Specifies the beam azimuth sweep to determine scan volume"""
        self.beamElevationCenter = 0
        """ Specifies the beam elevation center to determine scan volume"""
        self.beamElevationSweep = 0
        """ Specifies the beam elevation sweep to determine scan volume"""
        self.beamSweepSync = 0
        """ allows receiver to synchronize its regenerated scan pattern to that of the emmitter. Specifies the percentage of time a scan is through its pattern from its origion."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.beamAzimuthCenter);
        outputStream.write_null(self.beamAzimuthSweep);
        outputStream.write_null(self.beamElevationCenter);
        outputStream.write_null(self.beamElevationSweep);
        outputStream.write_null(self.beamSweepSync);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.beamAzimuthCenter = inputStream.read_null();
        self.beamAzimuthSweep = inputStream.read_null();
        self.beamElevationCenter = inputStream.read_null();
        self.beamElevationSweep = inputStream.read_null();
        self.beamSweepSync = inputStream.read_null();



class StopFreezeReliablePdu( object ):
    """alias, more descriptive name for a StopFreezeRPdu."""

    def __init__(self):
        """ Initializer for StopFreezeReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class SimulationIdentifier( object ):
    """The unique designation of a simulation when using the 48-bit identifier format shall be specified by the Sim- ulation Identifier record. The reason that the 48-bit format is required in addition to the 32-bit simulation address format that actually identifies a specific simulation is because some 48-bit identifier fields in PDUs may contain either an Object Identifier, such as an Entity ID, or a Simulation Identifier. Section 6.2.80"""

    def __init__(self):
        """ Initializer for SimulationIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ Simulation address """
        self.referenceNumber = 0
        """ This field shall be set to zero as there is no reference number associated with a Simulation Identifier."""

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.referenceNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.referenceNumber = inputStream.read_null();



class EventIdentifier( object ):
    """Identifies an event in the world. Use this format for every PDU EXCEPT the LiveEntityPdu. Section 6.2.34."""

    def __init__(self):
        """ Initializer for EventIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ Site and application IDs"""
        self.eventNumber = 0

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.eventNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.eventNumber = inputStream.read_null();



class LiveEntityOrientation16( object ):
    """16-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveEntityOrientation16"""
        self.psi = 0
        self.theta = 0
        self.phi = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.psi);
        outputStream.write_null(self.theta);
        outputStream.write_null(self.phi);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.psi = inputStream.read_null();
        self.theta = inputStream.read_null();
        self.phi = inputStream.read_null();



class LiveDeadReckoningParameters( object ):
    """16-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveDeadReckoningParameters"""
        self.entityLinearAcceleration = LEVector3FixedByte();
        self.entityAngularVelocity = LEVector3FixedByte();

    def serialize(self, outputStream):
        """serialize the class """
        self.entityLinearAcceleration.serialize(outputStream)
        self.entityAngularVelocity.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityLinearAcceleration.parse(inputStream)
        self.entityAngularVelocity.parse(inputStream)



class IntercomCommunicationsParameters( object ):
    """Intercom communications parameters. Section 6.2.46"""

    def __init__(self):
        """ Initializer for IntercomCommunicationsParameters"""
        self.recordLength = 0
        """ length of record"""
        self.recordSpecificField =  []
        """ This is a placeholder."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordLength);
        for idx in range(0, 0):
            outputStream.write_null( self.recordSpecificField[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordLength = inputStream.read_null();
        self.recordSpecificField = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordSpecificField[  idx  ] = val




class Munition( object ):
    """An entity's munition (e.g., bomb, missile) information shall be represented by one or more Munition records. For each type or location of munition, this record shall specify the type, location, quantity and status of munitions that an entity contains. Section 6.2.60 """

    def __init__(self):
        """ Initializer for Munition"""
        self.munitionType = EntityType();
        """  This field shall identify the entity type of the munition. See section 6.2.30."""
        self.station = 0
        """ The station or launcher to which the munition is assigned. See Annex I"""
        self.quantity = 0
        """ The quantity remaining of this munition."""
        self.padding = 0
        """ padding """

    def serialize(self, outputStream):
        """serialize the class """
        self.munitionType.serialize(outputStream)
        outputStream.write_null(self.station);
        outputStream.write_null(self.quantity);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.munitionType.parse(inputStream)
        self.station = inputStream.read_null();
        self.quantity = inputStream.read_null();
        self.padding = inputStream.read_null();



class AngularVelocityVector( object ):
    """Angular velocity measured in radians per second out each of the entity's own coordinate axes. Order of measurement is angular velocity around the x, y, and z axis of the entity. The positive direction is determined by the right hand rule. Section 6.2.7"""

    def __init__(self):
        """ Initializer for AngularVelocityVector"""
        self.x = 0
        """ velocity about the x axis"""
        self.y = 0
        """ velocity about the y axis"""
        self.z = 0
        """ velocity about the zaxis"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.x);
        outputStream.write_null(self.y);
        outputStream.write_null(self.z);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.x = inputStream.read_null();
        self.y = inputStream.read_null();
        self.z = inputStream.read_null();



class IntercomIdentifier( object ):
    """Unique designation of an attached or unattached intercom in an event or exercirse. Section 6.2.48"""

    def __init__(self):
        """ Initializer for IntercomIdentifier"""
        self.siteNumber = 0
        self.applicationNumber = 0
        self.referenceNumber = 0
        self.intercomNumber = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.siteNumber);
        outputStream.write_null(self.applicationNumber);
        outputStream.write_null(self.referenceNumber);
        outputStream.write_null(self.intercomNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.siteNumber = inputStream.read_null();
        self.applicationNumber = inputStream.read_null();
        self.referenceNumber = inputStream.read_null();
        self.intercomNumber = inputStream.read_null();



class StorageFuel( object ):
    """Information about an entity's engine fuel. Section 6.2.84."""

    def __init__(self):
        """ Initializer for StorageFuel"""
        self.fuelQuantity = 0
        """ Fuel quantity, units specified by next field"""
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.fuelQuantity);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.fuelQuantity = inputStream.read_null();
        self.padding = inputStream.read_null();



class Sensor( object ):
    """An entity's sensor information.  Section 6.2.77."""

    def __init__(self):
        """ Initializer for Sensor"""
        self.sensorType = 0
        """ for Source 'other':SensorRecordOtherActiveSensors/325,'em':EmitterName/75,'passive':SensorRecordSensorTypePassiveSensors/326,'mine':6.2.57,'ua':UAAcousticSystemName/144,'lasers':DesignatorSystemName/80"""
        self.station = 0
        """  the station to which the sensor is assigned. A zero value shall indi- cate that this Sensor record is not associated with any particular station and represents the total quan- tity of this sensor for this entity. If this field is non-zero, it shall either reference an attached part or an articulated part"""
        self.quantity = 0
        """ quantity of the sensor """
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.sensorType);
        outputStream.write_null(self.station);
        outputStream.write_null(self.quantity);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.sensorType = inputStream.read_null();
        self.station = inputStream.read_null();
        self.quantity = inputStream.read_null();
        self.padding = inputStream.read_null();



class ExpendableReload( object ):
    """An entity's expendable (chaff, flares, etc.) information. Section 6.2.37"""

    def __init__(self):
        """ Initializer for ExpendableReload"""
        self.expendable = EntityType();
        """ Type of expendable"""
        self.station = 0
        self.standardQuantity = 0
        self.maximumQuantity = 0
        self.standardQuantityReloadTime = 0
        self.maximumQuantityReloadTime = 0

    def serialize(self, outputStream):
        """serialize the class """
        self.expendable.serialize(outputStream)
        outputStream.write_null(self.station);
        outputStream.write_null(self.standardQuantity);
        outputStream.write_null(self.maximumQuantity);
        outputStream.write_null(self.standardQuantityReloadTime);
        outputStream.write_null(self.maximumQuantityReloadTime);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.expendable.parse(inputStream)
        self.station = inputStream.read_null();
        self.standardQuantity = inputStream.read_null();
        self.maximumQuantity = inputStream.read_null();
        self.standardQuantityReloadTime = inputStream.read_null();
        self.maximumQuantityReloadTime = inputStream.read_null();



class EntityIdentifier( object ):
    """Entity Identifier. Unique ID for entities in the world. Consists of an simulation address and a entity number. Section 6.2.28."""

    def __init__(self):
        """ Initializer for EntityIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ Site and application IDs"""
        self.entityNumber = 0
        """ Entity number"""

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.entityNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.entityNumber = inputStream.read_null();



class EntityID( object ):
    """also referred to as EntityIdentifier"""

    def __init__(self):
        """ Initializer for EntityID"""
        self.siteID = 0
        """ Site ID"""
        self.applicationID = 0
        """ application number ID"""
        self.entityID = 0
        """ Entity number ID"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.siteID);
        outputStream.write_null(self.applicationID);
        outputStream.write_null(self.entityID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.siteID = inputStream.read_null();
        self.applicationID = inputStream.read_null();
        self.entityID = inputStream.read_null();



class UnattachedIdentifier( object ):
    """The unique designation of one or more unattached radios in an event or exercise Section 6.2.91"""

    def __init__(self):
        """ Initializer for UnattachedIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ See 6.2.79"""
        self.referenceNumber = 0
        """ Reference number"""

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.referenceNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.referenceNumber = inputStream.read_null();



class GridAxisDescriptor( object ):
    """null"""

    def __init__(self):
        """ Initializer for GridAxisDescriptor"""
        self.domainInitialXi = 0
        """ coordinate of the grid origin or initial value"""
        self.domainFinalXi = 0
        """ coordinate of the endpoint or final value"""
        self.domainPointsXi = 0
        """ The number of grid points along the Xi domain axis for the enviornmental state data"""
        self.interleafFactor = 0
        """ interleaf factor along the domain axis."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.domainInitialXi);
        outputStream.write_null(self.domainFinalXi);
        outputStream.write_null(self.domainPointsXi);
        outputStream.write_null(self.interleafFactor);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.domainInitialXi = inputStream.read_null();
        self.domainFinalXi = inputStream.read_null();
        self.domainPointsXi = inputStream.read_null();
        self.interleafFactor = inputStream.read_null();



class EntityTypeVP( object ):
    """Association or disassociation of two entities.  Section 6.2.94.5"""

    def __init__(self):
        """ Initializer for EntityTypeVP"""
        self.entityType = EntityType();
        """ """
        self.padding = 0
        """ padding"""
        self.padding1 = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        self.entityType.serialize(outputStream)
        outputStream.write_null(self.padding);
        outputStream.write_null(self.padding1);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityType.parse(inputStream)
        self.padding = inputStream.read_null();
        self.padding1 = inputStream.read_null();



class BeamStatus( object ):
    """Information related to the status of a beam. This is contained in the beam status field of the electromagnitec emission PDU. The first bit determines whether the beam is active (0) or deactivated (1). Section 6.2.12."""

    def __init__(self):
        """ Initializer for BeamStatus"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class Pdu( object ):
    """Base class of PduBase and LiveEntityPdu"""

    def __init__(self):
        """ Initializer for Pdu"""
        self.exerciseID = 0
        """ Exercise ID"""
        self.timestamp = 0
        """ Timestamp value"""
        self.length = 0
        """ Length, in bytes, of the PDU"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.exerciseID);
        outputStream.write_null(self.timestamp);
        outputStream.write_null(self.length);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.exerciseID = inputStream.read_null();
        self.timestamp = inputStream.read_null();
        self.length = inputStream.read_null();



class VariableDatum( object ):
    """The variable datum type, the datum length, and the value for that variable datum type. Section 6.2.93"""

    def __init__(self):
        """ Initializer for VariableDatum"""
        self.variableDatumLength = 0
        """ Length, IN BITS, of the variable datum."""
        self.variableDatumValue =  []
        """ This can be any number of bits long, depending on the datum."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.variableDatumLength);
        for idx in range(0, 0):
            outputStream.write_null( self.variableDatumValue[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.variableDatumLength = inputStream.read_null();
        self.variableDatumValue = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.variableDatumValue[  idx  ] = val




class UABeam( object ):
    """null"""

    def __init__(self):
        """ Initializer for UABeam"""
        self.beamDataLength = 0
        self.beamNumber = 0
        self.padding = 0
        self.fundamentalParameterData = UAFundamentalParameter();

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.beamDataLength);
        outputStream.write_null(self.beamNumber);
        outputStream.write_null(self.padding);
        self.fundamentalParameterData.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.beamDataLength = inputStream.read_null();
        self.beamNumber = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.fundamentalParameterData.parse(inputStream)



class StandardVariableRecord( object ):
    """Section 6.2.83"""

    def __init__(self):
        """ Initializer for StandardVariableRecord"""
        self.recordLength = 0
        self.recordSpecificFields =  []

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordLength);
        for idx in range(0, 0):
            outputStream.write_null( self.recordSpecificFields[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordLength = inputStream.read_null();
        self.recordSpecificFields = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordSpecificFields[  idx  ] = val




class CommunicationsNodeID( object ):
    """Identity of a communications node. Section 6.2.48.4"""

    def __init__(self):
        """ Initializer for CommunicationsNodeID"""
        self.entityID = EntityID();
        self.elementID = 0

    def serialize(self, outputStream):
        """serialize the class """
        self.entityID.serialize(outputStream)
        outputStream.write_null(self.elementID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityID.parse(inputStream)
        self.elementID = inputStream.read_null();



class LiveEntityOrientation( object ):
    """8-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveEntityOrientation"""
        self.psi = 0
        self.theta = 0
        self.phi = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.psi);
        outputStream.write_null(self.theta);
        outputStream.write_null(self.phi);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.psi = inputStream.read_null();
        self.theta = inputStream.read_null();
        self.phi = inputStream.read_null();



class PropulsionSystemData( object ):
    """contains information describing the propulsion systems of the entity. This information shall be provided for each active propulsion system defined. Section 6.2.68"""

    def __init__(self):
        """ Initializer for PropulsionSystemData"""
        self.powerSetting = 0
        """ powerSetting"""
        self.engineRpm = 0
        """ engine RPMs"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.powerSetting);
        outputStream.write_null(self.engineRpm);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.powerSetting = inputStream.read_null();
        self.engineRpm = inputStream.read_null();



class ArticulatedPartVP( object ):
    """ articulated parts for movable parts and a combination of moveable/attached parts of an entity. Section 6.2.94.2"""

    def __init__(self):
        """ Initializer for ArticulatedPartVP"""
        self.changeIndicator = 0
        """ indicate the change of any parameter for any articulated part. Starts at zero, incremented for each change """
        self.partAttachedTo = 0
        """ The identification of the articulated part to which this articulation parameter is attached. This field shall be specified by a 16-bit unsigned integer. This field shall contain the value zero if the articulated part is attached directly to the entity."""
        self.parameterType = 0
        """ The type of parameter represented, 32-bit enumeration"""
        self.parameterValue = 0
        """ The definition of the 64-bits shall be determined based on the type of parameter specified in the Parameter Type field """
        self.padding = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.changeIndicator);
        outputStream.write_null(self.partAttachedTo);
        outputStream.write_null(self.parameterType);
        outputStream.write_null(self.parameterValue);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.changeIndicator = inputStream.read_null();
        self.partAttachedTo = inputStream.read_null();
        self.parameterType = inputStream.read_null();
        self.parameterValue = inputStream.read_null();
        self.padding = inputStream.read_null();



class LiveEntityIdentifier( object ):
    """The unique designation of each entity in an event or exercise that is contained in a Live Entity PDU. Section 6.2.54 """

    def __init__(self):
        """ Initializer for LiveEntityIdentifier"""
        self.liveSimulationAddress = LiveSimulationAddress();
        """ Live Simulation Address record (see 6.2.54) """
        self.entityNumber = 0
        """ Live entity number """

    def serialize(self, outputStream):
        """serialize the class """
        self.liveSimulationAddress.serialize(outputStream)
        outputStream.write_null(self.entityNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.liveSimulationAddress.parse(inputStream)
        self.entityNumber = inputStream.read_null();



class SeparationVP( object ):
    """Physical separation of an entity from another entity.  Section 6.2.94.6"""

    def __init__(self):
        """ Initializer for SeparationVP"""
        self.padding1 = 0
        """ padding"""
        self.parentEntityID = EntityID();
        """ ID of parent"""
        self.padding2 = 0
        """ padding"""
        self.stationLocation = NamedLocationIdentification();
        """ Station separated from"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.padding1);
        self.parentEntityID.serialize(outputStream)
        outputStream.write_null(self.padding2);
        self.stationLocation.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.padding1 = inputStream.read_null();
        self.parentEntityID.parse(inputStream)
        self.padding2 = inputStream.read_null();
        self.stationLocation.parse(inputStream)



class GridData( object ):
    """6.2.41, table 68"""

    def __init__(self):
        """ Initializer for GridData"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class IORecord( object ):
    """6.2.48"""

    def __init__(self):
        """ Initializer for IORecord"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class DataQueryDatumSpecification( object ):
    """List of fixed and variable datum records. Section 6.2.17 """

    def __init__(self):
        """ Initializer for DataQueryDatumSpecification"""
        self.numberOfFixedDatums = 0
        """ Number of fixed datums"""
        self.numberOfVariableDatums = 0
        """ Number of variable datums"""
        self.fixedDatumIDList = []
        """ variable length list fixed datum IDs"""
        self.variableDatumIDList = []
        """ variable length list variable datum IDs"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null( len(self.fixedDatumIDList));
        outputStream.write_null( len(self.variableDatumIDList));
        for anObj in self.fixedDatumIDList:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumIDList:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfFixedDatums = inputStream.read_null();
        self.numberOfVariableDatums = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatums):
            element = null()
            element.parse(inputStream)
            self.fixedDatumIDList.append(element)

        for idx in range(0, self.numberOfVariableDatums):
            element = null()
            element.parse(inputStream)
            self.variableDatumIDList.append(element)




class RadioIdentifier( object ):
    """The unique designation of an attached or unattached radio in an event or exercise Section 6.2.70"""

    def __init__(self):
        """ Initializer for RadioIdentifier"""
        self.siteNumber = 0
        """  site"""
        self.applicationNumber = 0
        """ application number"""
        self.referenceNumber = 0
        """  reference number"""
        self.radioNumber = 0
        """  Radio number"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.siteNumber);
        outputStream.write_null(self.applicationNumber);
        outputStream.write_null(self.referenceNumber);
        outputStream.write_null(self.radioNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.siteNumber = inputStream.read_null();
        self.applicationNumber = inputStream.read_null();
        self.referenceNumber = inputStream.read_null();
        self.radioNumber = inputStream.read_null();



class RequestID( object ):
    """A monotonically increasing number inserted into all simulation managment PDUs. This should be a hand-coded thingie, maybe a singleton. Section 6.2.75"""

    def __init__(self):
        """ Initializer for RequestID"""
        self.requestID = 0
        """ monotonically increasing number"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.requestID = inputStream.read_null();



class IFFData( object ):
    """repeating element in IFF Data specification record"""

    def __init__(self):
        """ Initializer for IFFData"""
        self.recordLength = 0
        """ length of record, including padding"""
        self.recordSpecificFields =  []

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordLength);
        for idx in range(0, 0):
            outputStream.write_null( self.recordSpecificFields[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordLength = inputStream.read_null();
        self.recordSpecificFields = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordSpecificFields[  idx  ] = val




class IntercomReferenceID( object ):
    """null"""

    def __init__(self):
        """ Initializer for IntercomReferenceID"""
        self.siteNumber = 0
        self.applicationNumber = 0
        self.referenceNumber = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.siteNumber);
        outputStream.write_null(self.applicationNumber);
        outputStream.write_null(self.referenceNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.siteNumber = inputStream.read_null();
        self.applicationNumber = inputStream.read_null();
        self.referenceNumber = inputStream.read_null();



class LiveEntityRelativeWorldCoordinates( object ):
    """16-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveEntityRelativeWorldCoordinates"""
        self.referencePoint = 0
        self.deltaX = 0
        self.deltaY = 0
        self.deltaZ = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.referencePoint);
        outputStream.write_null(self.deltaX);
        outputStream.write_null(self.deltaY);
        outputStream.write_null(self.deltaZ);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.referencePoint = inputStream.read_null();
        self.deltaX = inputStream.read_null();
        self.deltaY = inputStream.read_null();
        self.deltaZ = inputStream.read_null();



class AttachedPartVP( object ):
    """Removable parts that may be attached to an entity.  Section 6.2.93.3"""

    def __init__(self):
        """ Initializer for AttachedPartVP"""
        self.partAttachedTo = 0
        """ The identification of the articulated part to which this articulation parameter is attached. This field shall be specified by a 16-bit unsigned integer. This field shall contain the value zero if the articulated part is attached directly to the entity."""
        self.attachedPartType = EntityType();
        """ The definition of the 64-bits shall be determined based on the type of parameter specified in the Parameter Type field """

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.partAttachedTo);
        self.attachedPartType.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.partAttachedTo = inputStream.read_null();
        self.attachedPartType.parse(inputStream)



class GroupID( object ):
    """Unique designation of a group of entities contained in the isGroupOfPdu. Represents a group of entities rather than a single entity. Section 6.2.43"""

    def __init__(self):
        """ Initializer for GroupID"""
        self.simulationAddress = SimulationAddress();
        """ Simulation address (site and application number)"""
        self.groupNumber = 0
        """ group number"""

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.groupNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.groupNumber = inputStream.read_null();



class OwnershipStatusRecord( object ):
    """used to convey entity and conflict status information associated with transferring ownership of an entity. Section 6.2.65"""

    def __init__(self):
        """ Initializer for OwnershipStatusRecord"""
        self.entityId = EntityID();
        """ EntityID"""
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        self.entityId.serialize(outputStream)
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityId.parse(inputStream)
        self.padding = inputStream.read_null();



class UnsignedDISInteger( object ):
    """container class not in specification"""

    def __init__(self):
        """ Initializer for UnsignedDISInteger"""
        self.val = 0
        """ unsigned integer"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.val);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.val = inputStream.read_null();



class DeadReckoningParameters( object ):
    """Not specified in the standard. This is used by the ESPDU"""

    def __init__(self):
        """ Initializer for DeadReckoningParameters"""
        self.parameters =  [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        """ Dead reckoning parameters. Contents depends on algorithm."""
        self.entityLinearAcceleration = Vector3Float();
        """ Linear acceleration of the entity"""
        self.entityAngularVelocity = Vector3Float();
        """ Angular velocity of the entity"""

    def serialize(self, outputStream):
        """serialize the class """
        for idx in range(0, 15):
            outputStream.write_null( self.parameters[ idx ] );

        self.entityLinearAcceleration.serialize(outputStream)
        self.entityAngularVelocity.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.parameters = [0]*15
        for idx in range(0, 15):
            val = inputStream.read_null
            self.parameters[  idx  ] = val

        self.entityLinearAcceleration.parse(inputStream)
        self.entityAngularVelocity.parse(inputStream)



class ProtocolMode( object ):
    """Bit field used to identify minefield data. bits 14-15 are a 2-bit enum, other bits unused. Section 6.2.69"""

    def __init__(self):
        """ Initializer for ProtocolMode"""
        self.protocolMode = 0
        """ Bitfields, 14-15 contain an enum, uid 336"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.protocolMode);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.protocolMode = inputStream.read_null();



class FalseTargetsAttribute( object ):
    """The False Targets attribute record shall be used to communicate discrete values that are associated with false targets jamming that cannot be referenced to an emitter mode. The values provided in the False Targets attri- bute record shall be considered valid only for the victim radar beams listed in the jamming beam's Track/Jam Data records (provided in the associated Electromagnetic Emission PDU). Section 6.2.21.3"""

    def __init__(self):
        """ Initializer for FalseTargetsAttribute"""
        self.recordType = 3502
        """ record type"""
        self.recordLength = 40
        """ The length of the record in octets."""
        self.padding = 0
        """ padding"""
        self.emitterNumber = 0
        """ This field indicates the emitter system generating the false targets."""
        self.beamNumber = 0
        """ This field indicates the jamming beam generating the false targets. """
        self.padding2 = 0
        """ padding"""
        self.padding3 = 0
        self.falseTargetCount = 0
        """ This field indicates the jamming beam generating the false targets. """
        self.walkSpeed = 0
        """ This field shall specify the speed (in meters per second) at which false targets move toward the victim radar. Negative values shall indicate a velocity away from the victim radar. """
        self.walkAcceleration = 0
        """ This field shall specify the rate (in meters per second squared) at which false tar- gets accelerate toward the victim radar. Negative values shall indicate an acceleration direction away from the victim radar. """
        self.maximumWalkDistance = 0
        """ This field shall specify the distance (in meters) that a false target is to walk before it pauses in range. """
        self.keepTime = 0
        """ This field shall specify the time (in seconds) that a false target is to be held at the Maxi- mum Walk Distance before it resets to its initial position. """
        self.echoSpacing = 0
        """ This field shall specify the distance between false targets in meters. Positive values for this field shall indicate that second and subsequent false targets are initially placed at increasing ranges from the victim radar. """
        self.firstTargetOffset = 0
        """ Sets the position of the first false target relative to the jamming entity in meters."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        outputStream.write_null(self.emitterNumber);
        outputStream.write_null(self.beamNumber);
        outputStream.write_null(self.padding2);
        outputStream.write_null(self.padding3);
        outputStream.write_null(self.falseTargetCount);
        outputStream.write_null(self.walkSpeed);
        outputStream.write_null(self.walkAcceleration);
        outputStream.write_null(self.maximumWalkDistance);
        outputStream.write_null(self.keepTime);
        outputStream.write_null(self.echoSpacing);
        outputStream.write_null(self.firstTargetOffset);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.emitterNumber = inputStream.read_null();
        self.beamNumber = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.padding3 = inputStream.read_null();
        self.falseTargetCount = inputStream.read_null();
        self.walkSpeed = inputStream.read_null();
        self.walkAcceleration = inputStream.read_null();
        self.maximumWalkDistance = inputStream.read_null();
        self.keepTime = inputStream.read_null();
        self.echoSpacing = inputStream.read_null();
        self.firstTargetOffset = inputStream.read_null();



class MinefieldIdentifier( object ):
    """The unique designation of a minefield Section 6.2.56 """

    def __init__(self):
        """ Initializer for MinefieldIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ """
        self.minefieldNumber = 0
        """ """

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.minefieldNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.minefieldNumber = inputStream.read_null();



class NamedLocationIdentification( object ):
    """Information about the discrete positional relationship of the part entity with respect to the its host entity Section 6.2.62 """

    def __init__(self):
        """ Initializer for NamedLocationIdentification"""
        self.stationNumber = 0
        """ The number of the particular wing station, cargo hold etc., at which the part is attached. """

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.stationNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.stationNumber = inputStream.read_null();



class ModulationParameters( object ):
    """Modulation parameters associated with a specific radio system.  6.2.58 """

    def __init__(self):
        """ Initializer for ModulationParameters"""
        self.recordSpecificFields =  []

    def serialize(self, outputStream):
        """serialize the class """
        for idx in range(0, 0):
            outputStream.write_null( self.recordSpecificFields[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordSpecificFields = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.recordSpecificFields[  idx  ] = val




class CommentReliablePdu( object ):
    """alias, more descriptive name for a CommentRPdu."""

    def __init__(self):
        """ Initializer for CommentReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class EulerAngles( object ):
    """Three floating point values representing an orientation, psi, theta, and phi, aka the euler angles, in radians. Section 6.2.33"""

    def __init__(self):
        """ Initializer for EulerAngles"""
        self.psi = 0
        self.theta = 0
        self.phi = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.psi);
        outputStream.write_null(self.theta);
        outputStream.write_null(self.phi);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.psi = inputStream.read_null();
        self.theta = inputStream.read_null();
        self.phi = inputStream.read_null();



class DirectedEnergyPrecisionAimpoint( object ):
    """DE Precision Aimpoint Record. Section 6.2.20.3"""

    def __init__(self):
        """ Initializer for DirectedEnergyPrecisionAimpoint"""
        self.recordType = 4000
        """ Type of Record"""
        self.recordLength = 88
        """ Length of Record"""
        self.padding = 0
        """ Padding"""
        self.targetSpotLocation = Vector3Double();
        """ Position of Target Spot in World Coordinates."""
        self.targetSpotEntityLocation = Vector3Float();
        """ Position (meters) of Target Spot relative to Entity Position."""
        self.targetSpotVelocity = Vector3Float();
        """ Velocity (meters/sec) of Target Spot."""
        self.targetSpotAcceleration = Vector3Float();
        """ Acceleration (meters/sec/sec) of Target Spot."""
        self.targetEntityID = EntityID();
        """ Unique ID of the target entity."""
        self.targetComponentID = 0
        """ Target Component ID ENUM, same as in DamageDescriptionRecord."""
        self.beamSpotCrossSectionSemiMajorAxis = 0
        """ Beam Spot Cross Section Semi-Major Axis."""
        self.beamSpotCrossSectionSemiMinorAxis = 0
        """ Beam Spot Cross Section Semi-Major Axis."""
        self.beamSpotCrossSectionOrientationAngle = 0
        """ Beam Spot Cross Section Orientation Angle."""
        self.peakIrradiance = 0
        """ Peak irradiance"""
        self.padding2 = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        self.targetSpotLocation.serialize(outputStream)
        self.targetSpotEntityLocation.serialize(outputStream)
        self.targetSpotVelocity.serialize(outputStream)
        self.targetSpotAcceleration.serialize(outputStream)
        self.targetEntityID.serialize(outputStream)
        outputStream.write_null(self.targetComponentID);
        outputStream.write_null(self.beamSpotCrossSectionSemiMajorAxis);
        outputStream.write_null(self.beamSpotCrossSectionSemiMinorAxis);
        outputStream.write_null(self.beamSpotCrossSectionOrientationAngle);
        outputStream.write_null(self.peakIrradiance);
        outputStream.write_null(self.padding2);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.targetSpotLocation.parse(inputStream)
        self.targetSpotEntityLocation.parse(inputStream)
        self.targetSpotVelocity.parse(inputStream)
        self.targetSpotAcceleration.parse(inputStream)
        self.targetEntityID.parse(inputStream)
        self.targetComponentID = inputStream.read_null();
        self.beamSpotCrossSectionSemiMajorAxis = inputStream.read_null();
        self.beamSpotCrossSectionSemiMinorAxis = inputStream.read_null();
        self.beamSpotCrossSectionOrientationAngle = inputStream.read_null();
        self.peakIrradiance = inputStream.read_null();
        self.padding2 = inputStream.read_null();



class IffDataSpecification( object ):
    """Requires hand coding to be useful. Section 6.2.43"""

    def __init__(self):
        """ Initializer for IffDataSpecification"""
        self.numberOfIffDataRecords = 0
        """ Number of iff records"""
        self.iffDataRecords = []
        """ IFF data records"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null( len(self.iffDataRecords));
        for anObj in self.iffDataRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfIffDataRecords = inputStream.read_null();
        for idx in range(0, self.numberOfIffDataRecords):
            element = null()
            element.parse(inputStream)
            self.iffDataRecords.append(element)




class EntityTypeRaw( object ):
    """Identifies the type of Entity"""

    def __init__(self):
        """ Initializer for EntityTypeRaw"""
        self.domain = 0
        """ Domain of entity (air, surface, subsurface, space, etc.)"""
        self.country = 0
        """ country to which the design of the entity is attributed"""
        self.category = 0
        """ category of entity"""
        self.subCategory = 0
        """ subcategory of entity"""
        self.specific = 0
        """ specific info based on subcategory field. Renamed from specific because that is a reserved word in SQL."""
        self.extra = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.domain);
        outputStream.write_null(self.country);
        outputStream.write_null(self.category);
        outputStream.write_null(self.subCategory);
        outputStream.write_null(self.specific);
        outputStream.write_null(self.extra);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.domain = inputStream.read_null();
        self.country = inputStream.read_null();
        self.category = inputStream.read_null();
        self.subCategory = inputStream.read_null();
        self.specific = inputStream.read_null();
        self.extra = inputStream.read_null();



class RecordQueryReliablePdu( object ):
    """alias, more descriptive name for a RecordQueryRPdu."""

    def __init__(self):
        """ Initializer for RecordQueryReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class RecordQuerySpecification( object ):
    """The identification of the records being queried 6.2.72"""

    def __init__(self):
        """ Initializer for RecordQuerySpecification"""
        self.numberOfRecords = 0
        self.recordIDs = []
        """ variable length list of 32-bit record types uid = 66"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null( len(self.recordIDs));
        for anObj in self.recordIDs:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfRecords = inputStream.read_null();
        for idx in range(0, self.numberOfRecords):
            element = null()
            element.parse(inputStream)
            self.recordIDs.append(element)




class ObjectType( object ):
    """The unique designation of an environmental object. Section 6.2.64"""

    def __init__(self):
        """ Initializer for ObjectType"""
        self.category = 0
        """ category of entity"""
        self.subCategory = 0
        """ subcategory of entity"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.category);
        outputStream.write_null(self.subCategory);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.category = inputStream.read_null();
        self.subCategory = inputStream.read_null();



class LiveEntityDetonationPdu( object ):
    """alias, more descriptive name for a LEDetonationPdu."""

    def __init__(self):
        """ Initializer for LiveEntityDetonationPdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class AggregateIdentifier( object ):
    """The unique designation of each aggregate in an exercise is specified by an aggregate identifier record. The aggregate ID is not an entity and shall not be treated as such. Section 6.2.3."""

    def __init__(self):
        """ Initializer for AggregateIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ Simulation address, i.e. site and application, the first two fields of the entity ID"""
        self.aggregateID = 0
        """ The aggregate ID"""

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.aggregateID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.aggregateID = inputStream.read_null();



class EntityMarking( object ):
    """Specifies the character set used in the first byte, followed by 11 characters of text data. Section 6.29"""

    def __init__(self):
        """ Initializer for EntityMarking"""
        self.characters =  [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        """ The characters"""

    def serialize(self, outputStream):
        """serialize the class """
        for idx in range(0, 11):
            outputStream.write_null( self.characters[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.characters = [0]*11
        for idx in range(0, 11):
            val = inputStream.read_null
            self.characters[  idx  ] = val




class Appearance( object ):
    """used in AppearancePdu"""

    def __init__(self):
        """ Initializer for Appearance"""
        self.visual = 0
        self.ir = 0
        self.em = 0
        self.audio = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.visual);
        outputStream.write_null(self.ir);
        outputStream.write_null(self.em);
        outputStream.write_null(self.audio);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.visual = inputStream.read_null();
        self.ir = inputStream.read_null();
        self.em = inputStream.read_null();
        self.audio = inputStream.read_null();



class TotalRecordSets( object ):
    """Total number of record sets contained in a logical set of one or more PDUs. Used to transfer ownership, etc Section 6.2.88"""

    def __init__(self):
        """ Initializer for TotalRecordSets"""
        self.totalRecordSets = 0
        """ Total number of record sets"""
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.totalRecordSets);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.totalRecordSets = inputStream.read_null();
        self.padding = inputStream.read_null();



class MineEntityIdentifier( object ):
    """The unique designation of a mine contained in the Minefield Data PDU. No espdus are issued for mine entities.  Section 6.2.55 """

    def __init__(self):
        """ Initializer for MineEntityIdentifier"""
        self.simulationAddress = SimulationAddress();
        """ """
        self.mineEntityNumber = 0
        """ """

    def serialize(self, outputStream):
        """serialize the class """
        self.simulationAddress.serialize(outputStream)
        outputStream.write_null(self.mineEntityNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.simulationAddress.parse(inputStream)
        self.mineEntityNumber = inputStream.read_null();



class JammingTechnique( object ):
    """Jamming technique. Section 6.2.49, uid 284"""

    def __init__(self):
        """ Initializer for JammingTechnique"""
        self.kind = 0
        self.category = 0
        self.subCategory = 0
        self.specific = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.kind);
        outputStream.write_null(self.category);
        outputStream.write_null(self.subCategory);
        outputStream.write_null(self.specific);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.kind = inputStream.read_null();
        self.category = inputStream.read_null();
        self.subCategory = inputStream.read_null();
        self.specific = inputStream.read_null();



class DatumSpecification( object ):
    """List of fixed and variable datum records. Section 6.2.18 """

    def __init__(self):
        """ Initializer for DatumSpecification"""
        self.numberOfFixedDatums = 0
        """ Number of fixed datums"""
        self.numberOfVariableDatums = 0
        """ Number of variable datums"""
        self.fixedDatumIDList = []
        """ variable length list fixed datums"""
        self.variableDatumIDList = []
        """ variable length list of variable datums"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null( len(self.fixedDatumIDList));
        outputStream.write_null( len(self.variableDatumIDList));
        for anObj in self.fixedDatumIDList:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumIDList:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfFixedDatums = inputStream.read_null();
        self.numberOfVariableDatums = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatums):
            element = null()
            element.parse(inputStream)
            self.fixedDatumIDList.append(element)

        for idx in range(0, self.numberOfVariableDatums):
            element = null()
            element.parse(inputStream)
            self.variableDatumIDList.append(element)




class ModulationType( object ):
    """Information about the type of modulation used for radio transmission. 6.2.59 """

    def __init__(self):
        """ Initializer for ModulationType"""
        self.spreadSpectrum = 0
        """ This field shall indicate the spread spectrum technique or combination of spread spectrum techniques in use. Bit field. 0=freq hopping, 1=psuedo noise, time hopping=2, reamining bits unused"""
        self.detail = 0
        """ provide certain detailed information depending upon the major modulation type, uid 156-162"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.spreadSpectrum);
        outputStream.write_null(self.detail);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.spreadSpectrum = inputStream.read_null();
        self.detail = inputStream.read_null();



class SystemIdentifier( object ):
    """The ID of the IFF emitting system. Section 6.2.87"""

    def __init__(self):
        """ Initializer for SystemIdentifier"""
        self.changeOptions = ChangeOptions();
        """ status of this PDU, see section 6.2.15"""

    def serialize(self, outputStream):
        """serialize the class """
        self.changeOptions.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.changeOptions.parse(inputStream)



class CreateEntityReliablePdu( object ):
    """alias, more descriptive name for a CreateEntityRPdu."""

    def __init__(self):
        """ Initializer for CreateEntityReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class EventReportReliablePdu( object ):
    """alias, more descriptive name for a EventReportRPdu."""

    def __init__(self):
        """ Initializer for EventReportReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class EngineFuel( object ):
    """Information about an entity's engine fuel. Section 6.2.24."""

    def __init__(self):
        """ Initializer for EngineFuel"""
        self.fuelQuantity = 0
        """ Fuel quantity, units specified by next field"""
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.fuelQuantity);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.fuelQuantity = inputStream.read_null();
        self.padding = inputStream.read_null();



class SupplyQuantity( object ):
    """ A supply, and the amount of that supply. Section 6.2.86"""

    def __init__(self):
        """ Initializer for SupplyQuantity"""
        self.supplyType = EntityType();
        """ Type of supply"""
        self.quantity = 0
        """ The number of units of a supply type. """

    def serialize(self, outputStream):
        """serialize the class """
        self.supplyType.serialize(outputStream)
        outputStream.write_null(self.quantity);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.supplyType.parse(inputStream)
        self.quantity = inputStream.read_null();



class ActionResponseReliablePdu( object ):
    """alias, more descriptive name for a ActionResponseRPdu."""

    def __init__(self):
        """ Initializer for ActionResponseReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class SilentEntitySystem( object ):
    """information abou an enitity not producing espdus. Section 6.2.79"""

    def __init__(self):
        """ Initializer for SilentEntitySystem"""
        self.numberOfEntities = 0
        """ number of the type specified by the entity type field"""
        self.numberOfAppearanceRecords = 0
        """ number of entity appearance records that follow"""
        self.entityType = EntityType();
        """ Entity type"""
        self.appearanceRecordList =  []
        """ Variable length list of appearance records"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.numberOfEntities);
        outputStream.write_null(self.numberOfAppearanceRecords);
        self.entityType.serialize(outputStream)
        for idx in range(0, 0):
            outputStream.write_null( self.appearanceRecordList[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfEntities = inputStream.read_null();
        self.numberOfAppearanceRecords = inputStream.read_null();
        self.entityType.parse(inputStream)
        self.appearanceRecordList = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.appearanceRecordList[  idx  ] = val




class ActionRequestReliablePdu( object ):
    """alias, more descriptive name for a ActionRequestRPdu."""

    def __init__(self):
        """ Initializer for ActionRequestReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class BlankingSector( object ):
    """The Blanking Sector attribute record may be used to convey persistent areas within a scan volume where emitter power for a specific active emitter beam is reduced to an insignificant value. Section 6.2.21.2"""

    def __init__(self):
        """ Initializer for BlankingSector"""
        self.recordType = 3500
        """ record type"""
        self.recordLength = 40
        """ The length of the Blanking Sector attribute record in octets."""
        self.padding = 0
        """ Padding"""
        self.emitterNumber = 0
        """ indicates the emitter system for which the blanking sector values are applicable"""
        self.beamNumber = 0
        """ indicates the beam for which the blanking sector values are applicable."""
        self.padding2 = 0
        """ Padding"""
        self.leftAzimuth = 0
        """ This field is provided to indicate the left-most azimuth (clockwise in radians) for which emitted power is reduced. This angle is measured in the X-Y plane of the radar's entity coor- dinate system (see 1.4.3). The range of permissible values is 0 to 2PI, with zero pointing in the X- direction. """
        self.rightAzimuth = 0
        """ Indicate the right-most azimuth (clockwise in radians) for which emitted power is reduced. This angle is measured in the X-Y plane of the radar's entity coordinate system (see 1.4.3). The range of permissible values is 0 to 2PI , with zero pointing in the X- direction."""
        self.lowerElevation = 0
        """ This field is provided to indicate the lowest elevation (in radians) for which emit- ted power is reduced. This angle is measured positive upward with respect to the X-Y plane of the radar's entity coordinate system (see 1.4.3). The range of permissible values is -PI/2 to PI/2"""
        self.upperElevation = 0
        """ This field is provided to indicate the highest elevation (in radians) for which emitted power is reduced. This angle is measured positive upward with respect to the X-Y plane of the radar's entitycoordinatesystem(see1.4.3). The range of permissible values is -PI/2 to PI/2"""
        self.residualPower = 0
        """ This field shall specify the residual effective radiated power in the blanking sector in dBm. """
        self.padding3 = 0
        """ Padding, 32-bits"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.recordType);
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        outputStream.write_null(self.emitterNumber);
        outputStream.write_null(self.beamNumber);
        outputStream.write_null(self.padding2);
        outputStream.write_null(self.leftAzimuth);
        outputStream.write_null(self.rightAzimuth);
        outputStream.write_null(self.lowerElevation);
        outputStream.write_null(self.upperElevation);
        outputStream.write_null(self.residualPower);
        outputStream.write_null(self.padding3);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.recordType = inputStream.read_null();
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.emitterNumber = inputStream.read_null();
        self.beamNumber = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.leftAzimuth = inputStream.read_null();
        self.rightAzimuth = inputStream.read_null();
        self.lowerElevation = inputStream.read_null();
        self.upperElevation = inputStream.read_null();
        self.residualPower = inputStream.read_null();
        self.padding3 = inputStream.read_null();



class LiveEntityLinearVelocity( object ):
    """16-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveEntityLinearVelocity"""
        self.xComponent = 0
        self.yComponent = 0
        self.zComponent = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.xComponent);
        outputStream.write_null(self.yComponent);
        outputStream.write_null(self.zComponent);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.xComponent = inputStream.read_null();
        self.yComponent = inputStream.read_null();
        self.zComponent = inputStream.read_null();



class LaunchedMunitionRecord( object ):
    """Identity of a communications node. Section 6.2.50"""

    def __init__(self):
        """ Initializer for LaunchedMunitionRecord"""
        self.fireEventID = EventIdentifier();
        self.padding = 0
        self.firingEntityID = EntityID();
        self.padding2 = 0
        self.targetEntityID = EntityID();
        self.padding3 = 0
        self.targetLocation = Vector3Double();

    def serialize(self, outputStream):
        """serialize the class """
        self.fireEventID.serialize(outputStream)
        outputStream.write_null(self.padding);
        self.firingEntityID.serialize(outputStream)
        outputStream.write_null(self.padding2);
        self.targetEntityID.serialize(outputStream)
        outputStream.write_null(self.padding3);
        self.targetLocation.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.fireEventID.parse(inputStream)
        self.padding = inputStream.read_null();
        self.firingEntityID.parse(inputStream)
        self.padding2 = inputStream.read_null();
        self.targetEntityID.parse(inputStream)
        self.padding3 = inputStream.read_null();
        self.targetLocation.parse(inputStream)



class IFFFundamentalParameterData( object ):
    """Fundamental IFF atc data. Section 6.2.45"""

    def __init__(self):
        """ Initializer for IFFFundamentalParameterData"""
        self.erp = 0
        """ ERP"""
        self.frequency = 0
        """ frequency"""
        self.pgrf = 0
        """ pgrf"""
        self.pulseWidth = 0
        """ Pulse width"""
        self.burstLength = 0
        """ Burst length"""
        self.systemSpecificData =  [ 0, 0, 0]
        """ System-specific data"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.erp);
        outputStream.write_null(self.frequency);
        outputStream.write_null(self.pgrf);
        outputStream.write_null(self.pulseWidth);
        outputStream.write_null(self.burstLength);
        for idx in range(0, 3):
            outputStream.write_null( self.systemSpecificData[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.erp = inputStream.read_null();
        self.frequency = inputStream.read_null();
        self.pgrf = inputStream.read_null();
        self.pulseWidth = inputStream.read_null();
        self.burstLength = inputStream.read_null();
        self.systemSpecificData = [0]*3
        for idx in range(0, 3):
            val = inputStream.read_null
            self.systemSpecificData[  idx  ] = val




class FundamentalOperationalData( object ):
    """Basic operational data for IFF. Section 6.2.39"""

    def __init__(self):
        """ Initializer for FundamentalOperationalData"""
        self.systemStatus = 0
        """ system status, IEEE DIS 7 defined"""
        self.dataField1 = 0
        """ data field 1"""
        self.informationLayers = 0
        """ eight boolean fields"""
        self.dataField2 = 0
        """ enumeration"""
        self.parameter1 = 0
        """ parameter, enumeration"""
        self.parameter2 = 0
        """ parameter, enumeration"""
        self.parameter3 = 0
        """ parameter, enumeration"""
        self.parameter4 = 0
        """ parameter, enumeration"""
        self.parameter5 = 0
        """ parameter, enumeration"""
        self.parameter6 = 0
        """ parameter, enumeration"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.systemStatus);
        outputStream.write_null(self.dataField1);
        outputStream.write_null(self.informationLayers);
        outputStream.write_null(self.dataField2);
        outputStream.write_null(self.parameter1);
        outputStream.write_null(self.parameter2);
        outputStream.write_null(self.parameter3);
        outputStream.write_null(self.parameter4);
        outputStream.write_null(self.parameter5);
        outputStream.write_null(self.parameter6);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.systemStatus = inputStream.read_null();
        self.dataField1 = inputStream.read_null();
        self.informationLayers = inputStream.read_null();
        self.dataField2 = inputStream.read_null();
        self.parameter1 = inputStream.read_null();
        self.parameter2 = inputStream.read_null();
        self.parameter3 = inputStream.read_null();
        self.parameter4 = inputStream.read_null();
        self.parameter5 = inputStream.read_null();
        self.parameter6 = inputStream.read_null();



class EntityType( object ):
    """Identifies the type of Entity"""

    def __init__(self):
        """ Initializer for EntityType"""
        self.domain = Domain();
        """ Domain of entity (air, surface, subsurface, space, etc.)"""
        self.category = 0
        """ category of entity"""
        self.subCategory = 0
        """ subcategory based on category"""
        self.specific = 0
        """ specific info based on subcategory"""
        self.extra = 0

    def serialize(self, outputStream):
        """serialize the class """
        self.domain.serialize(outputStream)
        outputStream.write_null(self.category);
        outputStream.write_null(self.subCategory);
        outputStream.write_null(self.specific);
        outputStream.write_null(self.extra);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.domain.parse(inputStream)
        self.category = inputStream.read_null();
        self.subCategory = inputStream.read_null();
        self.specific = inputStream.read_null();
        self.extra = inputStream.read_null();



class StandardVariableSpecification( object ):
    """Does not work, and causes failure in anything it is embedded in. Section 6.2.83"""

    def __init__(self):
        """ Initializer for StandardVariableSpecification"""
        self.numberOfStandardVariableRecords = 0
        """ Number of static variable records"""
        self.standardVariables = []
        """ variable length list of standard variables, The class type and length here are WRONG and will cause the incorrect serialization of any class in whihc it is embedded."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null( len(self.standardVariables));
        for anObj in self.standardVariables:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfStandardVariableRecords = inputStream.read_null();
        for idx in range(0, self.numberOfStandardVariableRecords):
            element = null()
            element.parse(inputStream)
            self.standardVariables.append(element)




class UAEmitter( object ):
    """null"""

    def __init__(self):
        """ Initializer for UAEmitter"""
        self.systemDataLength = 0
        """  this field shall specify the length of this emitter system's data in 32-bit words."""
        self.numberOfBeams = 0
        """ the number of beams being described in the current PDU for the emitter system being described."""
        self.padding = 0
        self.acousticEmitter = AcousticEmitter();
        """ TODO"""
        self.location = Vector3Float();
        """ the location of the antenna beam source with respect to the emitting entity's coordinate system. This location shall be the origin of the emitter coordinate system that shall have the same orientation as the entity coordinate system. This field shall be represented by an Entity Coordinate Vector record see 6.2.95 """
        self.beams = []
        """ Electronic emission beams"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.systemDataLength);
        outputStream.write_null( len(self.beams));
        outputStream.write_null(self.padding);
        self.acousticEmitter.serialize(outputStream)
        self.location.serialize(outputStream)
        for anObj in self.beams:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.systemDataLength = inputStream.read_null();
        self.numberOfBeams = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.acousticEmitter.parse(inputStream)
        self.location.parse(inputStream)
        for idx in range(0, self.numberOfBeams):
            element = null()
            element.parse(inputStream)
            self.beams.append(element)




class SetRecordReliablePdu( object ):
    """alias, more descriptive name for a SetRecordRPdu."""

    def __init__(self):
        """ Initializer for SetRecordReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class Vector2Float( object ):
    """Two floating point values, x, y"""

    def __init__(self):
        """ Initializer for Vector2Float"""
        self.x = 0
        """ X value"""
        self.y = 0
        """ y Value"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.x);
        outputStream.write_null(self.y);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.x = inputStream.read_null();
        self.y = inputStream.read_null();



class Environment( object ):
    """ Information about a geometry, a state associated with a geometry, a bounding volume, or an associated entity ID.  6.2.31, not fully defined. 'The current definitions can be found in DIS PCR 240'"""

    def __init__(self):
        """ Initializer for Environment"""
        self.length = 0
        """ length, in bits"""
        self.index = 0
        """ Identify the sequentially numbered record index"""
        self.padding1 = 0
        """ padding"""
        self.geometry =  []
        """ Geometry or state record"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.length);
        outputStream.write_null(self.index);
        outputStream.write_null(self.padding1);
        for idx in range(0, 0):
            outputStream.write_null( self.geometry[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.length = inputStream.read_null();
        self.index = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.geometry = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.geometry[  idx  ] = val




class AcousticEmitter( object ):
    """Information about a specific UA emitter. Section 6.2.2."""

    def __init__(self):
        """ Initializer for AcousticEmitter"""
        self.acousticIDNumber = 0
        """ The UA emitter identification number relative to a specific system"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.acousticIDNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.acousticIDNumber = inputStream.read_null();



class AggregateMarking( object ):
    """Specifies the character set used in the first byte, followed by up to 31 characters of text data. Section 6.2.4. """

    def __init__(self):
        """ Initializer for AggregateMarking"""
        self.characters =  [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        """ The characters"""

    def serialize(self, outputStream):
        """serialize the class """
        for idx in range(0, 31):
            outputStream.write_null( self.characters[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.characters = [0]*31
        for idx in range(0, 31):
            val = inputStream.read_null
            self.characters[  idx  ] = val




class DataFilterRecord( object ):
    """identify which of the optional data fields are contained in the Minefield Data PDU or requested in the Minefield Query PDU. This is a 32-bit record. For each field, true denotes that the data is requested or present and false denotes that the data is neither requested nor present. Section 6.2.16"""

    def __init__(self):
        """ Initializer for DataFilterRecord"""
        self.bitFlags = 0
        """ Bitflags field"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.bitFlags);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.bitFlags = inputStream.read_null();



class MunitionReload( object ):
    """indicate weapons (munitions) previously communicated via the Munition record. Section 6.2.61 """

    def __init__(self):
        """ Initializer for MunitionReload"""
        self.munitionType = EntityType();
        """  This field shall identify the entity type of the munition. See section 6.2.30."""
        self.station = 0
        """ The station or launcher to which the munition is assigned. See Annex I"""
        self.standardQuantity = 0
        """ The standard quantity of this munition type normally loaded at this station/launcher if a station/launcher is specified."""
        self.maximumQuantity = 0
        """ The maximum quantity of this munition type that this station/launcher is capable of holding when a station/launcher is specified """
        self.standardQuantityReloadTime = 0
        """ numer of seconds of sim time required to reload the std qty"""
        self.maximumQuantityReloadTime = 0
        """ The number of seconds of sim time required to reload the max possible quantity"""

    def serialize(self, outputStream):
        """serialize the class """
        self.munitionType.serialize(outputStream)
        outputStream.write_null(self.station);
        outputStream.write_null(self.standardQuantity);
        outputStream.write_null(self.maximumQuantity);
        outputStream.write_null(self.standardQuantityReloadTime);
        outputStream.write_null(self.maximumQuantityReloadTime);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.munitionType.parse(inputStream)
        self.station = inputStream.read_null();
        self.standardQuantity = inputStream.read_null();
        self.maximumQuantity = inputStream.read_null();
        self.standardQuantityReloadTime = inputStream.read_null();
        self.maximumQuantityReloadTime = inputStream.read_null();



class LiveEntityPositionError( object ):
    """16-bit fixed binaries"""

    def __init__(self):
        """ Initializer for LiveEntityPositionError"""
        self.horizontalError = 0
        self.verticalError = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.horizontalError);
        outputStream.write_null(self.verticalError);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.horizontalError = inputStream.read_null();
        self.verticalError = inputStream.read_null();



class StorageFuelReload( object ):
    """For each type or location of Storage Fuel, this record shall specify the type, location, fuel measure- ment units, reload quantity and maximum quantity for storage fuel either for the whole entity or a specific storage fuel location (tank). Section 6.2.85."""

    def __init__(self):
        """ Initializer for StorageFuelReload"""
        self.standardQuantity = 0
        """  the standard quantity of this fuel type normally loaded at this station/launcher if a station/launcher is specified. If the Station/Launcher field is set to zero, then this is the total quantity of this fuel type that would be present in a standard reload of all appli- cable stations/launchers associated with this entity."""
        self.maximumQuantity = 0
        """ The maximum quantity of this fuel type that this sta- tion/launcher is capable of holding when a station/launcher is specified. This would be the value used when a maximum reload was desired to be set for this station/launcher. If the Station/launcher field is set to zero, then this is the maximum quantity of this fuel type that would be present on this entity at all stations/launchers that can accept this fuel type."""
        self.standardQuantityReloadTime = 0
        """ The seconds normally required to reload the standard quantity of this fuel type at this specific station/launcher. When the Station/Launcher field is set to zero, this shall be the time it takes to perform a standard quantity reload of this fuel type at all applicable stations/launchers for this entity."""
        self.maximumQuantityReloadTime = 0
        """ The seconds normally required to reload the maximum possible quantity of this fuel type at this station/launcher. When the Station/Launcher field is set to zero, this shall be the time it takes to perform a maximum quantity load/reload of this fuel type at all applicable stations/launchers for this entity."""
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.standardQuantity);
        outputStream.write_null(self.maximumQuantity);
        outputStream.write_null(self.standardQuantityReloadTime);
        outputStream.write_null(self.maximumQuantityReloadTime);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.standardQuantity = inputStream.read_null();
        self.maximumQuantity = inputStream.read_null();
        self.standardQuantityReloadTime = inputStream.read_null();
        self.maximumQuantityReloadTime = inputStream.read_null();
        self.padding = inputStream.read_null();



class SupplementalEmissionEntityStatePdu( object ):
    """alias, more descriptive name for a SEESPdu."""

    def __init__(self):
        """ Initializer for SupplementalEmissionEntityStatePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class TimeSpacePositionInformationPdu( object ):
    """alias, more descriptive name for a TSPIPdu."""

    def __init__(self):
        """ Initializer for TimeSpacePositionInformationPdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class DirectedEnergyTargetEnergyDeposition( object ):
    """DE energy depostion properties for a target entity. Section 6.2.20.4"""

    def __init__(self):
        """ Initializer for DirectedEnergyTargetEnergyDeposition"""
        self.targetEntityID = EntityID();
        """ Unique ID of the target entity."""
        self.padding = 0
        """ padding"""
        self.peakIrradiance = 0
        """ Peak irradiance"""

    def serialize(self, outputStream):
        """serialize the class """
        self.targetEntityID.serialize(outputStream)
        outputStream.write_null(self.padding);
        outputStream.write_null(self.peakIrradiance);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.targetEntityID.parse(inputStream)
        self.padding = inputStream.read_null();
        self.peakIrradiance = inputStream.read_null();



class MineEmplacementTime( object ):
    """null"""

    def __init__(self):
        """ Initializer for MineEmplacementTime"""
        self.hour = 0
        self.timePastTheHour = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.hour);
        outputStream.write_null(self.timePastTheHour);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.hour = inputStream.read_null();
        self.timePastTheHour = inputStream.read_null();



class EngineFuelReload( object ):
    """For each type or location of engine fuell, this record specifies the type, location, fuel measurement units, and reload quantity and maximum quantity. Section 6.2.25."""

    def __init__(self):
        """ Initializer for EngineFuelReload"""
        self.standardQuantity = 0
        """ standard quantity of fuel loaded"""
        self.maximumQuantity = 0
        """ maximum quantity of fuel loaded"""
        self.standardQuantityReloadTime = 0
        """ seconds normally required to to reload standard qty"""
        self.maximumQuantityReloadTime = 0
        """ seconds normally required to to reload maximum qty"""
        self.padding = 0
        """ padding"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.standardQuantity);
        outputStream.write_null(self.maximumQuantity);
        outputStream.write_null(self.standardQuantityReloadTime);
        outputStream.write_null(self.maximumQuantityReloadTime);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.standardQuantity = inputStream.read_null();
        self.maximumQuantity = inputStream.read_null();
        self.standardQuantityReloadTime = inputStream.read_null();
        self.maximumQuantityReloadTime = inputStream.read_null();
        self.padding = inputStream.read_null();



class ShaftRPM( object ):
    """Current Shaft RPM, Ordered Shaft RPM for use by Underwater Acoustic (UA) PDU. Section 7.6.4"""

    def __init__(self):
        """ Initializer for ShaftRPM"""
        self.currentRPM = 0
        self.orderedRPM = 0
        self.RPMrateOfChange = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.currentRPM);
        outputStream.write_null(self.orderedRPM);
        outputStream.write_null(self.RPMrateOfChange);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.currentRPM = inputStream.read_null();
        self.orderedRPM = inputStream.read_null();
        self.RPMrateOfChange = inputStream.read_null();



class SetDataReliablePdu( object ):
    """alias, more descriptive name for a SetDataRPdu."""

    def __init__(self):
        """ Initializer for SetDataReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class Vector3Double( object ):
    """Three double precision floating point values, x, y, and z. Used for world coordinates Section 6.2.97."""

    def __init__(self):
        """ Initializer for Vector3Double"""
        self.x = 0
        """ X value"""
        self.y = 0
        """ y Value"""
        self.z = 0
        """ Z value"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.x);
        outputStream.write_null(self.y);
        outputStream.write_null(self.z);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.x = inputStream.read_null();
        self.y = inputStream.read_null();
        self.z = inputStream.read_null();



class RecordSpecification( object ):
    """This record shall specify the number of record sets contained in the Record Specification record and the record details. Section 6.2.73."""

    def __init__(self):
        """ Initializer for RecordSpecification"""
        self.numberOfRecordSets = 0
        """ The number of record sets"""
        self.recordSets = []
        """ variable length list record specifications."""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null( len(self.recordSets));
        for anObj in self.recordSets:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.numberOfRecordSets = inputStream.read_null();
        for idx in range(0, self.numberOfRecordSets):
            element = null()
            element.parse(inputStream)
            self.recordSets.append(element)




class RecordReliablePdu( object ):
    """alias, more descriptive name for a RecordRPdu."""

    def __init__(self):
        """ Initializer for RecordReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class EventIdentifierLiveEntity( object ):
    """Identifies an event in the world. Use this format for ONLY the LiveEntityPdu. Section 6.2.34."""

    def __init__(self):
        """ Initializer for EventIdentifierLiveEntity"""
        self.siteNumber = 0
        self.applicationNumber = 0
        self.eventNumber = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.siteNumber);
        outputStream.write_null(self.applicationNumber);
        outputStream.write_null(self.eventNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.siteNumber = inputStream.read_null();
        self.applicationNumber = inputStream.read_null();
        self.eventNumber = inputStream.read_null();



class ExpendableDescriptor( object ):
    """Burst of chaff or expendible device. Section 6.2.19.4"""

    def __init__(self):
        """ Initializer for ExpendableDescriptor"""
        self.expendableType = EntityType();
        """ Type of the object that exploded"""
        self.padding = 0
        """ Padding"""

    def serialize(self, outputStream):
        """serialize the class """
        self.expendableType.serialize(outputStream)
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.expendableType.parse(inputStream)
        self.padding = inputStream.read_null();



class APA( object ):
    """Additional Passive Activity for use by Underwater Acoustic (UA) PDU. Section 7.6.4"""

    def __init__(self):
        """ Initializer for APA"""
        self.parameterIndex = 0
        self.value = 0

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.parameterIndex);
        outputStream.write_null(self.value);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.parameterIndex = inputStream.read_null();
        self.value = inputStream.read_null();



class EntityAssociationVP( object ):
    """Association or disassociation of two entities.  Section 6.2.94.4.3"""

    def __init__(self):
        """ Initializer for EntityAssociationVP"""
        self.entityID = EntityID();
        """ Object ID of entity associated with this entity"""
        self.groupNumber = 0
        """ Group if any to which the entity belongs"""

    def serialize(self, outputStream):
        """serialize the class """
        self.entityID.serialize(outputStream)
        outputStream.write_null(self.groupNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.entityID.parse(inputStream)
        self.groupNumber = inputStream.read_null();



class EmitterSystem( object ):
    """This field shall specify information about a particular emitter system. Section 6.2.23."""

    def __init__(self):
        """ Initializer for EmitterSystem"""
        self.emitterIDNumber = 0
        """ emitter ID, 8-bit enumeration"""

    def serialize(self, outputStream):
        """serialize the class """
        outputStream.write_null(self.emitterIDNumber);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        self.emitterIDNumber = inputStream.read_null();



class AcknowledgeReliablePdu( object ):
    """alias, more descriptive name for a AcknowledgeRPdu."""

    def __init__(self):
        """ Initializer for AcknowledgeReliablePdu"""

    def serialize(self, outputStream):
        """serialize the class """


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""




class GridAxisDescriptorVariable( GridAxisDescriptor ):
    """Grid axis descriptor fo variable spacing axis data."""

    def __init__(self):
        """ Initializer for GridAxisDescriptorVariable"""
        super(GridAxisDescriptorVariable, self).__init__()
        self.numberOfPointsOnXiAxis = 0
        """ Number of grid locations along Xi axis"""
        self.initialIndex = 0
        """ initial grid point for the current pdu"""
        self.coordinateScaleXi = 0
        """ value that linearly scales the coordinates of the grid locations for the xi axis"""
        self.coordinateOffsetXi = 0.0
        """ The constant offset value that shall be applied to the grid locations for the xi axis"""
        self.xiValues =  []
        """ list of coordinates"""

    def serialize(self, outputStream):
        """serialize the class """
        super( GridAxisDescriptorVariable, self ).serialize(outputStream)
        outputStream.write_null(self.numberOfPointsOnXiAxis);
        outputStream.write_null(self.initialIndex);
        outputStream.write_null(self.coordinateScaleXi);
        outputStream.write_null(self.coordinateOffsetXi);
        for idx in range(0, 0):
            outputStream.write_null( self.xiValues[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( GridAxisDescriptorVariable, self).parse(inputStream)
        self.numberOfPointsOnXiAxis = inputStream.read_null();
        self.initialIndex = inputStream.read_null();
        self.coordinateScaleXi = inputStream.read_null();
        self.coordinateOffsetXi = inputStream.read_null();
        self.xiValues = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.xiValues[  idx  ] = val




class GridAxisDescriptorFixed( GridAxisDescriptor ):
    """Grid axis record for fixed data. Section 6.2.41"""

    def __init__(self):
        """ Initializer for GridAxisDescriptorFixed"""
        super(GridAxisDescriptorFixed, self).__init__()
        self.numberOfPointsOnXiAxis = 0
        """ Number of grid locations along Xi axis"""
        self.initialIndex = 0
        """ initial grid point for the current pdu"""

    def serialize(self, outputStream):
        """serialize the class """
        super( GridAxisDescriptorFixed, self ).serialize(outputStream)
        outputStream.write_null(self.numberOfPointsOnXiAxis);
        outputStream.write_null(self.initialIndex);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( GridAxisDescriptorFixed, self).parse(inputStream)
        self.numberOfPointsOnXiAxis = inputStream.read_null();
        self.initialIndex = inputStream.read_null();



class PduBase( Pdu ):
    """The superclass for all PDUs except LiveEntity. This incorporates the PduHeader record, section 7.2.2"""

    def __init__(self):
        """ Initializer for PduBase"""
        super(PduBase, self).__init__()
        self.pduStatus = PduStatus();
        """ PDU Status Record. Described in 6.2.67. This field is not present in earlier DIS versions """
        self.padding = 0
        """ zero-filled array of padding"""

    def serialize(self, outputStream):
        """serialize the class """
        super( PduBase, self ).serialize(outputStream)
        self.pduStatus.serialize(outputStream)
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( PduBase, self).parse(inputStream)
        self.pduStatus.parse(inputStream)
        self.padding = inputStream.read_null();



class MinefieldFamilyPdu( PduBase ):
    """ Abstract superclass for PDUs relating to minefields. Section 7.9"""

    def __init__(self):
        """ Initializer for MinefieldFamilyPdu"""
        super(MinefieldFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.MINEFIELD
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( MinefieldFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( MinefieldFamilyPdu, self).parse(inputStream)



class MinefieldDataPdu( MinefieldFamilyPdu ):
    """5.10.4 Information about the location and status of a collection of mines in a minefield is conveyed through the Minefield Data PDU on an individual mine basis."""

    def __init__(self):
        """ Initializer for MinefieldDataPdu"""
        super(MinefieldDataPdu, self).__init__()
        self.minefieldID = MinefieldIdentifier();
        """ Minefield ID"""
        self.requestingEntityID = SimulationIdentifier();
        """ ID of entity making request"""
        self.minefieldSequenceNumbeer = 0
        """ Minefield sequence number"""
        self.requestID = 0
        """ request ID"""
        self.pduSequenceNumber = 0
        """ pdu sequence number"""
        self.numberOfPdus = 0
        """ number of pdus in response"""
        self.numberOfMinesInThisPdu = 0
        """ how many mines are in this PDU"""
        self.numberOfSensorTypes = 0
        """ how many sensor type are in this PDU"""
        self.padding = 0
        """ padding"""
        self.dataFilter = DataFilterRecord();
        """ 32 boolean field"""
        self.mineType = EntityType();
        """ Mine type"""
        self.sensorTypes = []
        """ Sensor types, each 16-bits long"""
        self.mineLocation = []
        """ Mine locations"""
        self.groundBurialDepthOffset =  []
        self.waterBurialDepthOffset =  []
        self.snowBurialDepthOffset =  []
        self.mineOrientation = []
        self.thermalContrast =  []
        self.reflectance =  []
        self.mineEmplacementTime = []
        self.mineEntityNumber =  []
        self.fusing = []
        """  uid 192"""
        self.scalarDetectionCoefficient =  []
        self.paintScheme = []
        """  uid 202"""
        self.numberOfTripDetonationWires =  []
        self.numberOfVertices =  []
        self.pduType = DisPduType.MINEFIELD_DATA
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( MinefieldDataPdu, self ).serialize(outputStream)
        self.minefieldID.serialize(outputStream)
        self.requestingEntityID.serialize(outputStream)
        outputStream.write_null(self.minefieldSequenceNumbeer);
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.pduSequenceNumber);
        outputStream.write_null(self.numberOfPdus);
        outputStream.write_null(self.numberOfMinesInThisPdu);
        outputStream.write_null( len(self.sensorTypes));
        outputStream.write_null(self.padding);
        self.dataFilter.serialize(outputStream)
        self.mineType.serialize(outputStream)
        for anObj in self.sensorTypes:
            anObj.serialize(outputStream)

        for anObj in self.mineLocation:
            anObj.serialize(outputStream)

        for idx in range(0, 0):
            outputStream.write_null( self.groundBurialDepthOffset[ idx ] );

        for idx in range(0, 0):
            outputStream.write_null( self.waterBurialDepthOffset[ idx ] );

        for idx in range(0, 0):
            outputStream.write_null( self.snowBurialDepthOffset[ idx ] );

        for anObj in self.mineOrientation:
            anObj.serialize(outputStream)

        for idx in range(0, 0):
            outputStream.write_null( self.thermalContrast[ idx ] );

        for idx in range(0, 0):
            outputStream.write_null( self.reflectance[ idx ] );

        for anObj in self.mineEmplacementTime:
            anObj.serialize(outputStream)

        for idx in range(0, 0):
            outputStream.write_null( self.mineEntityNumber[ idx ] );

        for anObj in self.fusing:
            anObj.serialize(outputStream)

        for idx in range(0, 0):
            outputStream.write_null( self.scalarDetectionCoefficient[ idx ] );

        for anObj in self.paintScheme:
            anObj.serialize(outputStream)

        for idx in range(0, 0):
            outputStream.write_null( self.numberOfTripDetonationWires[ idx ] );

        for idx in range(0, 0):
            outputStream.write_null( self.numberOfVertices[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( MinefieldDataPdu, self).parse(inputStream)
        self.minefieldID.parse(inputStream)
        self.requestingEntityID.parse(inputStream)
        self.minefieldSequenceNumbeer = inputStream.read_null();
        self.requestID = inputStream.read_null();
        self.pduSequenceNumber = inputStream.read_null();
        self.numberOfPdus = inputStream.read_null();
        self.numberOfMinesInThisPdu = inputStream.read_null();
        self.numberOfSensorTypes = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.dataFilter.parse(inputStream)
        self.mineType.parse(inputStream)
        for idx in range(0, self.numberOfSensorTypes):
            element = null()
            element.parse(inputStream)
            self.sensorTypes.append(element)

        for idx in range(0, self.numberOfMinesInThisPdu):
            element = null()
            element.parse(inputStream)
            self.mineLocation.append(element)

        self.groundBurialDepthOffset = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.groundBurialDepthOffset[  idx  ] = val

        self.waterBurialDepthOffset = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.waterBurialDepthOffset[  idx  ] = val

        self.snowBurialDepthOffset = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.snowBurialDepthOffset[  idx  ] = val

        for idx in range(0, self.numberOfMinesInThisPdu):
            element = null()
            element.parse(inputStream)
            self.mineOrientation.append(element)

        self.thermalContrast = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.thermalContrast[  idx  ] = val

        self.reflectance = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.reflectance[  idx  ] = val

        for idx in range(0, self.numberOfMinesInThisPdu):
            element = null()
            element.parse(inputStream)
            self.mineEmplacementTime.append(element)

        self.mineEntityNumber = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.mineEntityNumber[  idx  ] = val

        for idx in range(0, self.numberOfMinesInThisPdu):
            element = null()
            element.parse(inputStream)
            self.fusing.append(element)

        self.scalarDetectionCoefficient = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.scalarDetectionCoefficient[  idx  ] = val

        for idx in range(0, self.numberOfMinesInThisPdu):
            element = null()
            element.parse(inputStream)
            self.paintScheme.append(element)

        self.numberOfTripDetonationWires = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.numberOfTripDetonationWires[  idx  ] = val

        self.numberOfVertices = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.numberOfVertices[  idx  ] = val




class DistributedEmissionsRegenerationFamilyPdu( PduBase ):
    """Section 5.3.7. Electronic Emissions. Abstract superclass for distributed emissions PDU"""

    def __init__(self):
        """ Initializer for DistributedEmissionsRegenerationFamilyPdu"""
        super(DistributedEmissionsRegenerationFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.DISTRIBUTED_EMISSION_REGENERATION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DistributedEmissionsRegenerationFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DistributedEmissionsRegenerationFamilyPdu, self).parse(inputStream)



class LiveEntityFamilyPdu( Pdu ):
    """Does not inherit from PduBase.  See section 9."""

    def __init__(self):
        """ Initializer for LiveEntityFamilyPdu"""
        super(LiveEntityFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.LIVE_ENTITY_LE_INFORMATION_INTERACTION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( LiveEntityFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( LiveEntityFamilyPdu, self).parse(inputStream)



class AppearancePdu( LiveEntityFamilyPdu ):
    """9.4.3 Communicate information about the appearance of a live entity."""

    def __init__(self):
        """ Initializer for AppearancePdu"""
        super(AppearancePdu, self).__init__()
        self.liveEntityId = EntityID();
        self.appearanceFlags = 0
        """ 16-bit bit field"""
        self.entityType = EntityType();
        self.alternateEntityType = EntityType();
        self.entityMarking = EntityMarking();
        self.appearanceFields = Appearance();
        self.pduType = DisPduType.APPEARANCE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( AppearancePdu, self ).serialize(outputStream)
        self.liveEntityId.serialize(outputStream)
        outputStream.write_null(self.appearanceFlags);
        self.entityType.serialize(outputStream)
        self.alternateEntityType.serialize(outputStream)
        self.entityMarking.serialize(outputStream)
        self.appearanceFields.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( AppearancePdu, self).parse(inputStream)
        self.liveEntityId.parse(inputStream)
        self.appearanceFlags = inputStream.read_null();
        self.entityType.parse(inputStream)
        self.alternateEntityType.parse(inputStream)
        self.entityMarking.parse(inputStream)
        self.appearanceFields.parse(inputStream)



class LogisticsFamilyPdu( PduBase ):
    """Abstract superclass for logistics PDUs. Section 7.4"""

    def __init__(self):
        """ Initializer for LogisticsFamilyPdu"""
        super(LogisticsFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.LOGISTICS
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( LogisticsFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( LogisticsFamilyPdu, self).parse(inputStream)



class MinefieldStatePdu( MinefieldFamilyPdu ):
    """5.10.2 Communicate information about the minefield, including the location, perimeter, and types of mines contained within it."""

    def __init__(self):
        """ Initializer for MinefieldStatePdu"""
        super(MinefieldStatePdu, self).__init__()
        self.minefieldID = MinefieldIdentifier();
        """ Minefield ID"""
        self.minefieldSequence = 0
        """ Minefield sequence"""
        self.numberOfPerimeterPoints = 0
        """ Number of permieter points"""
        self.minefieldType = EntityType();
        """ type of minefield"""
        self.numberOfMineTypes = 0
        """ how many mine types"""
        self.minefieldLocation = Vector3Double();
        """ location of center of minefield in world coords"""
        self.minefieldOrientation = EulerAngles();
        """ orientation of minefield"""
        self.protocolMode = ProtocolMode();
        """ protocolMode. First two bits are the protocol mode, 14 bits reserved."""
        self.perimeterPoints = []
        """ perimeter points for the minefield"""
        self.mineType = []
        """ Type of mines"""
        self.pduType = DisPduType.MINEFIELD_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( MinefieldStatePdu, self ).serialize(outputStream)
        self.minefieldID.serialize(outputStream)
        outputStream.write_null(self.minefieldSequence);
        outputStream.write_null( len(self.perimeterPoints));
        self.minefieldType.serialize(outputStream)
        outputStream.write_null( len(self.mineType));
        self.minefieldLocation.serialize(outputStream)
        self.minefieldOrientation.serialize(outputStream)
        self.protocolMode.serialize(outputStream)
        for anObj in self.perimeterPoints:
            anObj.serialize(outputStream)

        for anObj in self.mineType:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( MinefieldStatePdu, self).parse(inputStream)
        self.minefieldID.parse(inputStream)
        self.minefieldSequence = inputStream.read_null();
        self.numberOfPerimeterPoints = inputStream.read_null();
        self.minefieldType.parse(inputStream)
        self.numberOfMineTypes = inputStream.read_null();
        self.minefieldLocation.parse(inputStream)
        self.minefieldOrientation.parse(inputStream)
        self.protocolMode.parse(inputStream)
        for idx in range(0, self.numberOfPerimeterPoints):
            element = null()
            element.parse(inputStream)
            self.perimeterPoints.append(element)

        for idx in range(0, self.numberOfMineTypes):
            element = null()
            element.parse(inputStream)
            self.mineType.append(element)




class RepairCompletePdu( LogisticsFamilyPdu ):
    """5.5.10 Used by the repairing entity to communicate the repair that has been performed for the entity that requested repair service."""

    def __init__(self):
        """ Initializer for RepairCompletePdu"""
        super(RepairCompletePdu, self).__init__()
        self.receivingEntityID = EntityID();
        """ Entity that is receiving service.  See 6.2.28"""
        self.repairingEntityID = EntityID();
        """ Entity that is supplying.  See 6.2.28"""
        self.padding4 = 0
        """ padding, number prevents conflict with superclass ivar name"""
        self.pduType = DisPduType.REPAIR_COMPLETE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RepairCompletePdu, self ).serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        self.repairingEntityID.serialize(outputStream)
        outputStream.write_null(self.padding4);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RepairCompletePdu, self).parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.repairingEntityID.parse(inputStream)
        self.padding4 = inputStream.read_null();



class SyntheticEnvironmentFamilyPdu( PduBase ):
    """Section 5.3.11: Abstract superclass for synthetic environment PDUs"""

    def __init__(self):
        """ Initializer for SyntheticEnvironmentFamilyPdu"""
        super(SyntheticEnvironmentFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.SYNTHETIC_ENVIRONMENT
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SyntheticEnvironmentFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SyntheticEnvironmentFamilyPdu, self).parse(inputStream)



class RepairResponsePdu( LogisticsFamilyPdu ):
    """5.5.11 used by the receiving entity to acknowledge the receipt of a Repair Complete PDU"""

    def __init__(self):
        """ Initializer for RepairResponsePdu"""
        super(RepairResponsePdu, self).__init__()
        self.receivingEntityID = EntityID();
        """ Entity that requested repairs.  See 6.2.28"""
        self.repairingEntityID = EntityID();
        """ Entity that is repairing.  See 6.2.28"""
        self.padding1 = 0
        """ padding"""
        self.padding2 = 0
        """ padding"""
        self.pduType = DisPduType.REPAIR_RESPONSE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RepairResponsePdu, self ).serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        self.repairingEntityID.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RepairResponsePdu, self).parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.repairingEntityID.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();



class MinefieldResponseNACKPdu( MinefieldFamilyPdu ):
    """5.10.5 Contains information about the requesting entity and the PDU(s) that were not received in response to a query. NACK = Negative Acknowledgment."""

    def __init__(self):
        """ Initializer for MinefieldResponseNACKPdu"""
        super(MinefieldResponseNACKPdu, self).__init__()
        self.minefieldID = MinefieldIdentifier();
        """ Minefield ID"""
        self.requestingEntityID = SimulationIdentifier();
        """ entity ID making the request"""
        self.requestID = 0
        """ request ID"""
        self.numberOfMissingPdus = 0
        """ how many pdus were missing"""
        self.missingPduSequenceNumbers =  []
        """ PDU sequence numbers that were missing"""
        self.pduType = DisPduType.MINEFIELD_RESPONSE_NACK
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( MinefieldResponseNACKPdu, self ).serialize(outputStream)
        self.minefieldID.serialize(outputStream)
        self.requestingEntityID.serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.numberOfMissingPdus);
        for idx in range(0, 0):
            outputStream.write_null( self.missingPduSequenceNumbers[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( MinefieldResponseNACKPdu, self).parse(inputStream)
        self.minefieldID.parse(inputStream)
        self.requestingEntityID.parse(inputStream)
        self.requestID = inputStream.read_null();
        self.numberOfMissingPdus = inputStream.read_null();
        self.missingPduSequenceNumbers = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.missingPduSequenceNumbers[  idx  ] = val




class RadioCommunicationsFamilyPdu( PduBase ):
    """ Abstract superclass for radio communications PDUs. Section 7.7"""

    def __init__(self):
        """ Initializer for RadioCommunicationsFamilyPdu"""
        super(RadioCommunicationsFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.RADIO_COMMUNICATIONS
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RadioCommunicationsFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RadioCommunicationsFamilyPdu, self).parse(inputStream)



class IntercomSignalPdu( RadioCommunicationsFamilyPdu ):
    """5.8.6 Conveys the audio or digital data that is used to communicate between simulated intercom devices"""

    def __init__(self):
        """ Initializer for IntercomSignalPdu"""
        super(IntercomSignalPdu, self).__init__()
        self.intercomReferenceID = IntercomReferenceID();
        self.intercomNumber = 0
        """ ID of communications device"""
        self.encodingScheme = 0
        """ encoding scheme"""
        self.sampleRate = 0
        """ sample rate"""
        self.dataLength = 0
        """ data length"""
        self.samples = 0
        """ samples"""
        self.data =  []
        """ data bytes"""
        self.pduType = DisPduType.INTERCOM_SIGNAL
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( IntercomSignalPdu, self ).serialize(outputStream)
        self.intercomReferenceID.serialize(outputStream)
        outputStream.write_null(self.intercomNumber);
        outputStream.write_null(self.encodingScheme);
        outputStream.write_null(self.sampleRate);
        outputStream.write_null(self.dataLength);
        outputStream.write_null(self.samples);
        for idx in range(0, 0):
            outputStream.write_null( self.data[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IntercomSignalPdu, self).parse(inputStream)
        self.intercomReferenceID.parse(inputStream)
        self.intercomNumber = inputStream.read_null();
        self.encodingScheme = inputStream.read_null();
        self.sampleRate = inputStream.read_null();
        self.dataLength = inputStream.read_null();
        self.samples = inputStream.read_null();
        self.data = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.data[  idx  ] = val




class ResupplyReceivedPdu( LogisticsFamilyPdu ):
    """5.5.7 Used to acknowledge the receipt of supplies by the receiving entity."""

    def __init__(self):
        """ Initializer for ResupplyReceivedPdu"""
        super(ResupplyReceivedPdu, self).__init__()
        self.receivingEntityID = EntityID();
        """ Entity that is receiving service.  Shall be represented by Entity Identifier record (see 6.2.28)"""
        self.supplyingEntityID = EntityID();
        """ Entity that is supplying.  Shall be represented by Entity Identifier record (see 6.2.28)"""
        self.numberOfSupplyTypes = 0
        """ How many supplies are taken by receiving entity"""
        self.padding1 = 0
        """ padding"""
        self.padding2 = 0
        """ padding"""
        self.supplies = []
        """ A Reord that Specifies the type of supply and the amount of that supply for each of the supply types in numberOfSupplyTypes (see 6.2.85), Section 7.4.3"""
        self.pduType = DisPduType.RESUPPLY_RECEIVED
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ResupplyReceivedPdu, self ).serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        self.supplyingEntityID.serialize(outputStream)
        outputStream.write_null( len(self.supplies));
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        for anObj in self.supplies:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ResupplyReceivedPdu, self).parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.supplyingEntityID.parse(inputStream)
        self.numberOfSupplyTypes = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        for idx in range(0, self.numberOfSupplyTypes):
            element = null()
            element.parse(inputStream)
            self.supplies.append(element)




class WarfareFamilyPdu( PduBase ):
    """Abstract superclass for fire and detonation pdus that have shared information. Section 7.3"""

    def __init__(self):
        """ Initializer for WarfareFamilyPdu"""
        super(WarfareFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.WARFARE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( WarfareFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( WarfareFamilyPdu, self).parse(inputStream)



class SimulationManagementWithReliabilityFamilyPdu( PduBase ):
    """Simulation Management with Reliability PDUs with reliability service levels in a DIS exercise are an alternative to the Simulation Management PDUs, and may or may not be required for participation in an DIS exercise,"""

    def __init__(self):
        """ Initializer for SimulationManagementWithReliabilityFamilyPdu"""
        super(SimulationManagementWithReliabilityFamilyPdu, self).__init__()
        self.originatingID = SimulationIdentifier();
        """ IDs the simulation or entity, either a simulation or an entity. Either 6.2.80 or 6.2.28"""
        self.receivingID = SimulationIdentifier();
        """ simulation, all simulations, a special ID, or an entity. See 5.6.5 and 5.12.4"""
        self.protocolFamily = DISProtocolFamily.SIMULATION_MANAGEMENT_WITH_RELIABILITY
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SimulationManagementWithReliabilityFamilyPdu, self ).serialize(outputStream)
        self.originatingID.serialize(outputStream)
        self.receivingID.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SimulationManagementWithReliabilityFamilyPdu, self).parse(inputStream)
        self.originatingID.parse(inputStream)
        self.receivingID.parse(inputStream)



class MinefieldQueryPdu( MinefieldFamilyPdu ):
    """5.10.3 Contains information about the requesting entity and the region and mine types of interest to the requesting entity."""

    def __init__(self):
        """ Initializer for MinefieldQueryPdu"""
        super(MinefieldQueryPdu, self).__init__()
        self.minefieldID = MinefieldIdentifier();
        """ Minefield ID"""
        self.requestingEntityID = EntityID();
        """ EID of entity making the request"""
        self.requestID = 0
        """ request ID"""
        self.numberOfPerimeterPoints = 0
        """ Number of perimeter points for the minefield"""
        self.padding = 0
        self.numberOfSensorTypes = 0
        """ Number of sensor types"""
        self.dataFilter = DataFilterRecord();
        """ data filter, 32 boolean fields"""
        self.requestedMineType = EntityType();
        """ Entity type of mine being requested"""
        self.requestedPerimeterPoints = []
        """ perimeter points of request"""
        self.sensorTypes = []
        """ Sensor types, each 16-bits long"""
        self.pduType = DisPduType.MINEFIELD_QUERY
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( MinefieldQueryPdu, self ).serialize(outputStream)
        self.minefieldID.serialize(outputStream)
        self.requestingEntityID.serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null( len(self.requestedPerimeterPoints));
        outputStream.write_null(self.padding);
        outputStream.write_null( len(self.sensorTypes));
        self.dataFilter.serialize(outputStream)
        self.requestedMineType.serialize(outputStream)
        for anObj in self.requestedPerimeterPoints:
            anObj.serialize(outputStream)

        for anObj in self.sensorTypes:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( MinefieldQueryPdu, self).parse(inputStream)
        self.minefieldID.parse(inputStream)
        self.requestingEntityID.parse(inputStream)
        self.requestID = inputStream.read_null();
        self.numberOfPerimeterPoints = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.numberOfSensorTypes = inputStream.read_null();
        self.dataFilter.parse(inputStream)
        self.requestedMineType.parse(inputStream)
        for idx in range(0, self.numberOfPerimeterPoints):
            element = null()
            element.parse(inputStream)
            self.requestedPerimeterPoints.append(element)

        for idx in range(0, self.numberOfSensorTypes):
            element = null()
            element.parse(inputStream)
            self.sensorTypes.append(element)




class LEFirePdu( LiveEntityFamilyPdu ):
    """9.4.5 Representation of weapons fire in a DIS exercise involving LEs."""

    def __init__(self):
        """ Initializer for LEFirePdu"""
        super(LEFirePdu, self).__init__()
        self.firingLiveEntityId = EntityID();
        self.flags = 0
        """ Bits defined in IEEE Standard"""
        self.targetLiveEntityId = EntityID();
        self.munitionLiveEntityId = EntityID();
        self.eventId = EventIdentifier();
        self.location = LiveEntityRelativeWorldCoordinates();
        self.munitionDescriptor = MunitionDescriptor();
        self.velocity = LiveEntityLinearVelocity();
        self.range = 0
        self.pduType = DisPduType.LIVE_ENTITY_FIRE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( LEFirePdu, self ).serialize(outputStream)
        self.firingLiveEntityId.serialize(outputStream)
        outputStream.write_null(self.flags);
        self.targetLiveEntityId.serialize(outputStream)
        self.munitionLiveEntityId.serialize(outputStream)
        self.eventId.serialize(outputStream)
        self.location.serialize(outputStream)
        self.munitionDescriptor.serialize(outputStream)
        self.velocity.serialize(outputStream)
        outputStream.write_null(self.range);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( LEFirePdu, self).parse(inputStream)
        self.firingLiveEntityId.parse(inputStream)
        self.flags = inputStream.read_null();
        self.targetLiveEntityId.parse(inputStream)
        self.munitionLiveEntityId.parse(inputStream)
        self.eventId.parse(inputStream)
        self.location.parse(inputStream)
        self.munitionDescriptor.parse(inputStream)
        self.velocity.parse(inputStream)
        self.range = inputStream.read_null();



class GriddedDataPdu( SyntheticEnvironmentFamilyPdu ):
    """7.10.3 Used to communicate information about global, spatially varying environmental effects."""

    def __init__(self):
        """ Initializer for GriddedDataPdu"""
        super(GriddedDataPdu, self).__init__()
        self.environmentalSimulationApplicationID = SimulationIdentifier();
        """ environmental simulation application ID"""
        self.fieldNumber = 0
        """ unique identifier for each piece of environmental data"""
        self.pduNumber = 0
        """ sequence number for the total set of PDUS used to transmit the data"""
        self.pduTotal = 0
        """ Total number of PDUS used to transmit the data"""
        self.numberOfGridAxes = 0
        """ number of grid axes for the environmental data"""
        self.environmentType = EntityType();
        """ type of environment"""
        self.orientation = EulerAngles();
        """ orientation of the data grid"""
        self.sampleTime = ClockTime();
        """ valid time of the enviormental data sample, 64-bit unsigned int"""
        self.totalValues = 0
        """ total number of all data values for all pdus for an environmental sample"""
        self.vectorDimension = 0
        """ total number of data values at each grid point."""
        self.padding1 = 0
        """ padding"""
        self.padding2 = 0
        """ padding"""
        self.gridAxisDescriptors = []
        """ """
        self.gridDataRecords = []
        """ """
        self.pduType = DisPduType.GRIDDED_DATA
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( GriddedDataPdu, self ).serialize(outputStream)
        self.environmentalSimulationApplicationID.serialize(outputStream)
        outputStream.write_null(self.fieldNumber);
        outputStream.write_null(self.pduNumber);
        outputStream.write_null(self.pduTotal);
        outputStream.write_null( len(self.gridDataRecords));
        self.environmentType.serialize(outputStream)
        self.orientation.serialize(outputStream)
        self.sampleTime.serialize(outputStream)
        outputStream.write_null(self.totalValues);
        outputStream.write_null(self.vectorDimension);
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        for anObj in self.gridAxisDescriptors:
            anObj.serialize(outputStream)

        for anObj in self.gridDataRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( GriddedDataPdu, self).parse(inputStream)
        self.environmentalSimulationApplicationID.parse(inputStream)
        self.fieldNumber = inputStream.read_null();
        self.pduNumber = inputStream.read_null();
        self.pduTotal = inputStream.read_null();
        self.numberOfGridAxes = inputStream.read_null();
        self.environmentType.parse(inputStream)
        self.orientation.parse(inputStream)
        self.sampleTime.parse(inputStream)
        self.totalValues = inputStream.read_null();
        self.vectorDimension = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        for idx in range(0, self.numberOfGridAxes):
            element = null()
            element.parse(inputStream)
            self.gridAxisDescriptors.append(element)

        for idx in range(0, self.numberOfGridAxes):
            element = null()
            element.parse(inputStream)
            self.gridDataRecords.append(element)




class ResupplyCancelPdu( LogisticsFamilyPdu ):
    """5.5.8 Used to communicate the canceling of a resupply service provided through logistics support."""

    def __init__(self):
        """ Initializer for ResupplyCancelPdu"""
        super(ResupplyCancelPdu, self).__init__()
        self.receivingEntityID = EntityID();
        """ Requesting entity, Section 7.4.5"""
        self.supplyingEntityID = EntityID();
        """ Supplying entity, Section 7.4.5"""
        self.pduType = DisPduType.RESUPPLY_CANCEL
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ResupplyCancelPdu, self ).serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        self.supplyingEntityID.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ResupplyCancelPdu, self).parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.supplyingEntityID.parse(inputStream)



class EntityManagementFamilyPdu( PduBase ):
    """ Managment of grouping of PDUs, and more. Section 7.8"""

    def __init__(self):
        """ Initializer for EntityManagementFamilyPdu"""
        super(EntityManagementFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.ENTITY_MANAGEMENT
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EntityManagementFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EntityManagementFamilyPdu, self).parse(inputStream)



class CreateEntityRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.2 Serves the same function as the Create Entity PDU but with the addition of reliability service levels."""

    def __init__(self):
        """ Initializer for CreateEntityRPdu"""
        super(CreateEntityRPdu, self).__init__()
        self.pad1 = 0
        self.pad2 = 0
        self.requestID = 0
        """ Request ID"""
        self.pduType = DisPduType.CREATE_ENTITY_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( CreateEntityRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( CreateEntityRPdu, self).parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.requestID = inputStream.read_null();



class ResupplyOfferPdu( LogisticsFamilyPdu ):
    """5.5.6 Communicate the offer of supplies by a supplying entity to a receiving entity."""

    def __init__(self):
        """ Initializer for ResupplyOfferPdu"""
        super(ResupplyOfferPdu, self).__init__()
        self.receivingEntityID = EntityID();
        """ Field identifies the Entity and respective Entity Record ID that is receiving service (see 6.2.28), Section 7.4.3"""
        self.supplyingEntityID = EntityID();
        """ Identifies the Entity and respective Entity ID Record that is supplying  (see 6.2.28), Section 7.4.3"""
        self.numberOfSupplyTypes = 0
        """ How many supplies types are being offered, Section 7.4.3"""
        self.padding1 = 0
        """ padding"""
        self.padding2 = 0
        """ padding"""
        self.supplies = []
        """ A Reord that Specifies the type of supply and the amount of that supply for each of the supply types in numberOfSupplyTypes (see 6.2.85), Section 7.4.3"""
        self.pduType = DisPduType.RESUPPLY_OFFER
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ResupplyOfferPdu, self ).serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        self.supplyingEntityID.serialize(outputStream)
        outputStream.write_null( len(self.supplies));
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        for anObj in self.supplies:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ResupplyOfferPdu, self).parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.supplyingEntityID.parse(inputStream)
        self.numberOfSupplyTypes = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        for idx in range(0, self.numberOfSupplyTypes):
            element = null()
            element.parse(inputStream)
            self.supplies.append(element)




class RecordRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.16 Used to respond to a Record Query-R PDU or a Set Record-R PDU. It is used to provide information requested in a Record Query-R PDU, to confirm the information received in a Set Record-R PDU, and to confirm the receipt of a periodic or unsolicited Record-R PDU when the acknowledged service level is used."""

    def __init__(self):
        """ Initializer for RecordRPdu"""
        super(RecordRPdu, self).__init__()
        self.requestID = 0
        """ request ID"""
        self.pad1 = 0
        self.numberOfRecordSets = 0
        """ Number of record sets in list"""
        self.recordSets = []
        """ record sets"""
        self.pduType = DisPduType.RECORD_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RecordRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.pad1);
        outputStream.write_null( len(self.recordSets));
        for anObj in self.recordSets:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RecordRPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.pad1 = inputStream.read_null();
        self.numberOfRecordSets = inputStream.read_null();
        for idx in range(0, self.numberOfRecordSets):
            element = null()
            element.parse(inputStream)
            self.recordSets.append(element)




class PointObjectStatePdu( SyntheticEnvironmentFamilyPdu ):
    """7.10.4 Used to communicate detailed information about the addition/modification of a synthetic environment object that is geometrically anchored to the terrain with a single point."""

    def __init__(self):
        """ Initializer for PointObjectStatePdu"""
        super(PointObjectStatePdu, self).__init__()
        self.objectID = EntityID();
        """ Object in synthetic environment"""
        self.referencedObjectID = ObjectIdentifier();
        """ Object with which this point object is associated"""
        self.updateNumber = 0
        """ unique update number of each state transition of an object"""
        self.objectType = ObjectType();
        """ Object type"""
        self.objectLocation = Vector3Double();
        """ Object location"""
        self.objectOrientation = EulerAngles();
        """ Object orientation"""
        self.specificObjectAppearance = 0
        """ Specific object apperance"""
        self.padding1 = 0
        self.requesterID = SimulationAddress();
        """ requesterID"""
        self.receivingID = SimulationAddress();
        """ receiver ID"""
        self.pad2 = 0
        """ padding"""
        self.pduType = DisPduType.POINT_OBJECT_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( PointObjectStatePdu, self ).serialize(outputStream)
        self.objectID.serialize(outputStream)
        self.referencedObjectID.serialize(outputStream)
        outputStream.write_null(self.updateNumber);
        self.objectType.serialize(outputStream)
        self.objectLocation.serialize(outputStream)
        self.objectOrientation.serialize(outputStream)
        outputStream.write_null(self.specificObjectAppearance);
        outputStream.write_null(self.padding1);
        self.requesterID.serialize(outputStream)
        self.receivingID.serialize(outputStream)
        outputStream.write_null(self.pad2);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( PointObjectStatePdu, self).parse(inputStream)
        self.objectID.parse(inputStream)
        self.referencedObjectID.parse(inputStream)
        self.updateNumber = inputStream.read_null();
        self.objectType.parse(inputStream)
        self.objectLocation.parse(inputStream)
        self.objectOrientation.parse(inputStream)
        self.specificObjectAppearance = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.requesterID.parse(inputStream)
        self.receivingID.parse(inputStream)
        self.pad2 = inputStream.read_null();



class UnderwaterAcousticPdu( DistributedEmissionsRegenerationFamilyPdu ):
    """7.6.4 Information about underwater acoustic emmissions. See 5.7.5."""

    def __init__(self):
        """ Initializer for UnderwaterAcousticPdu"""
        super(UnderwaterAcousticPdu, self).__init__()
        self.emittingEntityID = EntityID();
        """ ID of the entity that is the source of the emission"""
        self.eventID = EventIdentifier();
        """ ID of event"""
        self.pad = 0
        """ padding"""
        self.propulsionPlantConfiguration = 0
        """ This field shall specify the entity propulsion plant configuration. This field is used to determine the passive signature characteristics of an entity."""
        self.numberOfShafts = 0
        """  This field shall represent the number of shafts on a platform"""
        self.numberOfAPAs = 0
        """ This field shall indicate the number of APAs described in the current UA PDU"""
        self.numberOfUAEmitterSystems = 0
        """ This field shall specify the number of UA emitter systems being described in the current UA PDU"""
        self.shaftRPMs = []
        """ shaft RPM values."""
        self.apaData = []
        """ additional passive activities"""
        self.emitterSystems = []
        self.pduType = DisPduType.UNDERWATER_ACOUSTIC
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( UnderwaterAcousticPdu, self ).serialize(outputStream)
        self.emittingEntityID.serialize(outputStream)
        self.eventID.serialize(outputStream)
        outputStream.write_null(self.pad);
        outputStream.write_null(self.propulsionPlantConfiguration);
        outputStream.write_null( len(self.shaftRPMs));
        outputStream.write_null( len(self.apaData));
        outputStream.write_null( len(self.emitterSystems));
        for anObj in self.shaftRPMs:
            anObj.serialize(outputStream)

        for anObj in self.apaData:
            anObj.serialize(outputStream)

        for anObj in self.emitterSystems:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( UnderwaterAcousticPdu, self).parse(inputStream)
        self.emittingEntityID.parse(inputStream)
        self.eventID.parse(inputStream)
        self.pad = inputStream.read_null();
        self.propulsionPlantConfiguration = inputStream.read_null();
        self.numberOfShafts = inputStream.read_null();
        self.numberOfAPAs = inputStream.read_null();
        self.numberOfUAEmitterSystems = inputStream.read_null();
        for idx in range(0, self.numberOfShafts):
            element = null()
            element.parse(inputStream)
            self.shaftRPMs.append(element)

        for idx in range(0, self.numberOfAPAs):
            element = null()
            element.parse(inputStream)
            self.apaData.append(element)

        for idx in range(0, self.numberOfUAEmitterSystems):
            element = null()
            element.parse(inputStream)
            self.emitterSystems.append(element)




class IsGroupOfPdu( EntityManagementFamilyPdu ):
    """5.9.3.1 The IsGroupOf PDU shall communicate information about the individual states of a group of entities, including state information that is necessary for the receiving simulation applications to represent the issuing group of entities in the simulation applications’ own simulation."""

    def __init__(self):
        """ Initializer for IsGroupOfPdu"""
        super(IsGroupOfPdu, self).__init__()
        self.groupEntityID = EntityID();
        """ ID of aggregated entities"""
        self.numberOfGroupedEntities = 0
        """ Number of individual entities constituting the group"""
        self.pad = 0
        """ padding"""
        self.latitude = 0
        """ latitude"""
        self.longitude = 0
        """ longitude"""
        self.groupedEntityDescriptions = []
        """ GED records about each individual entity in the group. Bad specing--the Group Entity Descriptions are not described."""
        self.pduType = DisPduType.ISGROUPOF
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( IsGroupOfPdu, self ).serialize(outputStream)
        self.groupEntityID.serialize(outputStream)
        outputStream.write_null( len(self.groupedEntityDescriptions));
        outputStream.write_null(self.pad);
        outputStream.write_null(self.latitude);
        outputStream.write_null(self.longitude);
        for anObj in self.groupedEntityDescriptions:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IsGroupOfPdu, self).parse(inputStream)
        self.groupEntityID.parse(inputStream)
        self.numberOfGroupedEntities = inputStream.read_null();
        self.pad = inputStream.read_null();
        self.latitude = inputStream.read_null();
        self.longitude = inputStream.read_null();
        for idx in range(0, self.numberOfGroupedEntities):
            element = null()
            element.parse(inputStream)
            self.groupedEntityDescriptions.append(element)




class IFFPdu( DistributedEmissionsRegenerationFamilyPdu ):
    """7.6.5.1 Information about military and civilian interrogators, transponders, and specific other electronic systems. See 5.7.6"""

    def __init__(self):
        """ Initializer for IFFPdu"""
        super(IFFPdu, self).__init__()
        self.emittingEntityId = EntityID();
        """ ID of the entity that is the source of the emissions"""
        self.eventID = EventIdentifier();
        """ Number generated by the issuing simulation to associate realted events."""
        self.location = Vector3Float();
        """ Location wrt entity. There is some ambiguity in the standard here, but this is the order it is listed in the table."""
        self.systemID = SystemIdentifier();
        """ System ID information"""
        self.systemDesignator = 0
        self.systemSpecificData = 0
        self.fundamentalParameters = FundamentalOperationalData();
        """ fundamental parameters"""
        self.pduType = DisPduType.IDENTIFICATION_FRIEND_OR_FOE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( IFFPdu, self ).serialize(outputStream)
        self.emittingEntityId.serialize(outputStream)
        self.eventID.serialize(outputStream)
        self.location.serialize(outputStream)
        self.systemID.serialize(outputStream)
        outputStream.write_null(self.systemDesignator);
        outputStream.write_null(self.systemSpecificData);
        self.fundamentalParameters.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IFFPdu, self).parse(inputStream)
        self.emittingEntityId.parse(inputStream)
        self.eventID.parse(inputStream)
        self.location.parse(inputStream)
        self.systemID.parse(inputStream)
        self.systemDesignator = inputStream.read_null();
        self.systemSpecificData = inputStream.read_null();
        self.fundamentalParameters.parse(inputStream)



class ArealObjectStatePdu( SyntheticEnvironmentFamilyPdu ):
    """7.10.6 Used to communicate detailed information about the addition/modification of a synthetic environment object that is geometrically anchored to the terrain with a set of three or more points that come to a closure."""

    def __init__(self):
        """ Initializer for ArealObjectStatePdu"""
        super(ArealObjectStatePdu, self).__init__()
        self.objectID = ObjectIdentifier();
        """ Object in synthetic environment"""
        self.referencedObjectID = ObjectIdentifier();
        """ Object with which this point object is associated"""
        self.updateNumber = 0
        """ unique update number of each state transition of an object"""
        self.objectType = ObjectType();
        """ Object type"""
        self.specificObjectAppearance = 0
        """ Object appearance"""
        self.generalObjectAppearance = 0
        """ Object appearance"""
        self.numberOfPoints = 0
        """ Number of points"""
        self.requesterID = SimulationAddress();
        """ requesterID"""
        self.receivingID = SimulationAddress();
        """ receiver ID"""
        self.objectLocation = []
        """ location of object"""
        self.pduType = DisPduType.AREAL_OBJECT_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ArealObjectStatePdu, self ).serialize(outputStream)
        self.objectID.serialize(outputStream)
        self.referencedObjectID.serialize(outputStream)
        outputStream.write_null(self.updateNumber);
        self.objectType.serialize(outputStream)
        outputStream.write_null(self.specificObjectAppearance);
        outputStream.write_null(self.generalObjectAppearance);
        outputStream.write_null( len(self.objectLocation));
        self.requesterID.serialize(outputStream)
        self.receivingID.serialize(outputStream)
        for anObj in self.objectLocation:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ArealObjectStatePdu, self).parse(inputStream)
        self.objectID.parse(inputStream)
        self.referencedObjectID.parse(inputStream)
        self.updateNumber = inputStream.read_null();
        self.objectType.parse(inputStream)
        self.specificObjectAppearance = inputStream.read_null();
        self.generalObjectAppearance = inputStream.read_null();
        self.numberOfPoints = inputStream.read_null();
        self.requesterID.parse(inputStream)
        self.receivingID.parse(inputStream)
        for idx in range(0, self.numberOfPoints):
            element = null()
            element.parse(inputStream)
            self.objectLocation.append(element)




class ServiceRequestPdu( LogisticsFamilyPdu ):
    """5.5.5 Communicate information associated with one entity requesting a service from another."""

    def __init__(self):
        """ Initializer for ServiceRequestPdu"""
        super(ServiceRequestPdu, self).__init__()
        self.requestingEntityID = EntityID();
        """ Entity that is requesting service (see 6.2.28), Section 7.4.2"""
        self.servicingEntityID = EntityID();
        """ Entity that is providing the service (see 6.2.28), Section 7.4.2"""
        self.numberOfSupplyTypes = 0
        """ How many requested, Section 7.4.2"""
        self.padding1 = 0
        self.supplies = []
        self.pduType = DisPduType.SERVICE_REQUEST
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ServiceRequestPdu, self ).serialize(outputStream)
        self.requestingEntityID.serialize(outputStream)
        self.servicingEntityID.serialize(outputStream)
        outputStream.write_null( len(self.supplies));
        outputStream.write_null(self.padding1);
        for anObj in self.supplies:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ServiceRequestPdu, self).parse(inputStream)
        self.requestingEntityID.parse(inputStream)
        self.servicingEntityID.parse(inputStream)
        self.numberOfSupplyTypes = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        for idx in range(0, self.numberOfSupplyTypes):
            element = null()
            element.parse(inputStream)
            self.supplies.append(element)




class DirectedEnergyFirePdu( WarfareFamilyPdu ):
    """7.3.4 Used to communicate the firing of a directed energy weapon."""

    def __init__(self):
        """ Initializer for DirectedEnergyFirePdu"""
        super(DirectedEnergyFirePdu, self).__init__()
        self.firingEntityID = EntityID();
        """ ID of the entity that shot"""
        self.eventID = EventIdentifier();
        self.munitionType = EntityType();
        """ Field shall identify the munition type enumeration for the DE weapon beam, Section 7.3.4 """
        self.shotStartTime = ClockTime();
        """ Field shall indicate the simulation time at start of the shot, Section 7.3.4 """
        self.commulativeShotTime = 0
        """ Field shall indicate the current cumulative duration of the shot, Section 7.3.4 """
        self.apertureEmitterLocation = Vector3Float();
        """ Field shall identify the location of the DE weapon aperture/emitter, Section 7.3.4 """
        self.apertureDiameter = 0
        """ Field shall identify the beam diameter at the aperture/emitter, Section 7.3.4 """
        self.wavelength = 0
        """ Field shall identify the emissions wavelength in units of meters, Section 7.3.4 """
        self.pad1 = 0
        self.pulseRepititionFrequency = 0
        self.pulseWidth = 0
        """ field shall identify the pulse width emissions in units of seconds, Section 7.3.4"""
        self.pad2 = 0
        self.pad3 = 0
        self.pad4 = 0
        self.numberOfDERecords = 0
        """ Field shall specify the number of DE records, Section 7.3.4 """
        self.dERecords = []
        """ Fields shall contain one or more DE records, records shall conform to the variable record format (Section6.2.82), Section 7.3.4"""
        self.pduType = DisPduType.DIRECTED_ENERGY_FIRE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DirectedEnergyFirePdu, self ).serialize(outputStream)
        self.firingEntityID.serialize(outputStream)
        self.eventID.serialize(outputStream)
        self.munitionType.serialize(outputStream)
        self.shotStartTime.serialize(outputStream)
        outputStream.write_null(self.commulativeShotTime);
        self.apertureEmitterLocation.serialize(outputStream)
        outputStream.write_null(self.apertureDiameter);
        outputStream.write_null(self.wavelength);
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pulseRepititionFrequency);
        outputStream.write_null(self.pulseWidth);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.pad3);
        outputStream.write_null(self.pad4);
        outputStream.write_null( len(self.dERecords));
        for anObj in self.dERecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DirectedEnergyFirePdu, self).parse(inputStream)
        self.firingEntityID.parse(inputStream)
        self.eventID.parse(inputStream)
        self.munitionType.parse(inputStream)
        self.shotStartTime.parse(inputStream)
        self.commulativeShotTime = inputStream.read_null();
        self.apertureEmitterLocation.parse(inputStream)
        self.apertureDiameter = inputStream.read_null();
        self.wavelength = inputStream.read_null();
        self.pad1 = inputStream.read_null();
        self.pulseRepititionFrequency = inputStream.read_null();
        self.pulseWidth = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.pad3 = inputStream.read_null();
        self.pad4 = inputStream.read_null();
        self.numberOfDERecords = inputStream.read_null();
        for idx in range(0, self.numberOfDERecords):
            element = null()
            element.parse(inputStream)
            self.dERecords.append(element)




class AcknowledgeRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.6 Serves the same function as the Acknowledge PDU but is used to acknowledge the receipt of a Create Entity-R PDU, a Remove Entity-R PDU, a Start/Resume-R PDU, or a Stop/Freeze-R PDU."""

    def __init__(self):
        """ Initializer for AcknowledgeRPdu"""
        super(AcknowledgeRPdu, self).__init__()
        self.requestID = 0
        """ Request ID"""
        self.pduType = DisPduType.ACKNOWLEDGE_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( AcknowledgeRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( AcknowledgeRPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();



class SEESPdu( DistributedEmissionsRegenerationFamilyPdu ):
    """7.6.6 Certain supplemental information on an entity’s physical state and emissions. See 5.7.7"""

    def __init__(self):
        """ Initializer for SEESPdu"""
        super(SEESPdu, self).__init__()
        self.orginatingEntityID = EntityID();
        """ Originating entity ID"""
        self.infraredSignatureRepresentationIndex = 0
        """ IR Signature representation index"""
        self.acousticSignatureRepresentationIndex = 0
        """ acoustic Signature representation index"""
        self.radarCrossSectionSignatureRepresentationIndex = 0
        """ radar cross section representation index"""
        self.numberOfPropulsionSystems = 0
        """ how many propulsion systems"""
        self.numberOfVectoringNozzleSystems = 0
        """ how many vectoring nozzle systems"""
        self.propulsionSystemData = []
        """ variable length list of propulsion system data"""
        self.vectoringSystemData = []
        """ variable length list of vectoring system data"""
        self.pduType = DisPduType.SUPPLEMENTAL_EMISSION_ENTITY_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SEESPdu, self ).serialize(outputStream)
        self.orginatingEntityID.serialize(outputStream)
        outputStream.write_null(self.infraredSignatureRepresentationIndex);
        outputStream.write_null(self.acousticSignatureRepresentationIndex);
        outputStream.write_null(self.radarCrossSectionSignatureRepresentationIndex);
        outputStream.write_null( len(self.propulsionSystemData));
        outputStream.write_null( len(self.vectoringSystemData));
        for anObj in self.propulsionSystemData:
            anObj.serialize(outputStream)

        for anObj in self.vectoringSystemData:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SEESPdu, self).parse(inputStream)
        self.orginatingEntityID.parse(inputStream)
        self.infraredSignatureRepresentationIndex = inputStream.read_null();
        self.acousticSignatureRepresentationIndex = inputStream.read_null();
        self.radarCrossSectionSignatureRepresentationIndex = inputStream.read_null();
        self.numberOfPropulsionSystems = inputStream.read_null();
        self.numberOfVectoringNozzleSystems = inputStream.read_null();
        for idx in range(0, self.numberOfPropulsionSystems):
            element = null()
            element.parse(inputStream)
            self.propulsionSystemData.append(element)

        for idx in range(0, self.numberOfVectoringNozzleSystems):
            element = null()
            element.parse(inputStream)
            self.vectoringSystemData.append(element)




class DetonationPdu( WarfareFamilyPdu ):
    """7.3.3 Used to communicate the detonation or impact of munitions, as well as non-munition explosions, the burst or initial bloom of chaff, and the ignition of a flare."""

    def __init__(self):
        """ Initializer for DetonationPdu"""
        super(DetonationPdu, self).__init__()
        self.sourceEntityID = EntityID();
        """ ID of the entity that shot"""
        self.targetEntityID = EntityID();
        """ ID of the entity that is being shot at"""
        self.explodingEntityID = EntityID();
        """ ID of the expendable entity, Section 7.3.3 """
        self.eventID = EventIdentifier();
        """ ID of event, Section 7.3.3"""
        self.velocity = Vector3Float();
        """ velocity of the munition immediately before detonation/impact, Section 7.3.3 """
        self.locationInWorldCoordinates = Vector3Double();
        """ location of the munition detonation, the expendable detonation, Section 7.3.3 """
        self.descriptor = MunitionDescriptor();
        """ Describes the detonation represented, Section 7.3.3 """
        self.locationOfEntityCoordinates = Vector3Float();
        """ Velocity of the ammunition, Section 7.3.3 """
        self.numberOfVariableParameters = 0
        """ How many articulation parameters we have, Section 7.3.3 """
        self.pad = 0
        """ padding"""
        self.variableParameters = []
        """ specify the parameter values for each Variable Parameter record, Section 7.3.3 """
        self.pduType = DisPduType.DETONATION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DetonationPdu, self ).serialize(outputStream)
        self.sourceEntityID.serialize(outputStream)
        self.targetEntityID.serialize(outputStream)
        self.explodingEntityID.serialize(outputStream)
        self.eventID.serialize(outputStream)
        self.velocity.serialize(outputStream)
        self.locationInWorldCoordinates.serialize(outputStream)
        self.descriptor.serialize(outputStream)
        self.locationOfEntityCoordinates.serialize(outputStream)
        outputStream.write_null( len(self.variableParameters));
        outputStream.write_null(self.pad);
        for anObj in self.variableParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DetonationPdu, self).parse(inputStream)
        self.sourceEntityID.parse(inputStream)
        self.targetEntityID.parse(inputStream)
        self.explodingEntityID.parse(inputStream)
        self.eventID.parse(inputStream)
        self.velocity.parse(inputStream)
        self.locationInWorldCoordinates.parse(inputStream)
        self.descriptor.parse(inputStream)
        self.locationOfEntityCoordinates.parse(inputStream)
        self.numberOfVariableParameters = inputStream.read_null();
        self.pad = inputStream.read_null();
        for idx in range(0, self.numberOfVariableParameters):
            element = null()
            element.parse(inputStream)
            self.variableParameters.append(element)




class InformationOperationsFamilyPdu( PduBase ):
    """Information operations (IO) are the integrated employment of electronic warfare (EW), computer network operations (CNO), psychological operations (PSYOP), military deception (MILDEC), and operations security (OPSEC), along with specific supporting capabilities, to influence, disrupt, corrupt, or otherwise affect enemy information and decision making while protecting friendly information operations."""

    def __init__(self):
        """ Initializer for InformationOperationsFamilyPdu"""
        super(InformationOperationsFamilyPdu, self).__init__()
        self.originatingSimID = EntityID();
        """ Object originating the request"""
        self.protocolFamily = DISProtocolFamily.INFORMATION_OPERATIONS
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( InformationOperationsFamilyPdu, self ).serialize(outputStream)
        self.originatingSimID.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( InformationOperationsFamilyPdu, self).parse(inputStream)
        self.originatingSimID.parse(inputStream)



class SimulationManagementFamilyPdu( PduBase ):
    """First part of a simulation management (SIMAN) PDU and SIMAN-Reliability (SIMAN-R) PDU. Sectionn 6.2.81"""

    def __init__(self):
        """ Initializer for SimulationManagementFamilyPdu"""
        super(SimulationManagementFamilyPdu, self).__init__()
        self.originatingID = SimulationIdentifier();
        """ IDs the simulation or entity, either a simulation or an entity. Either 6.2.80 or 6.2.28"""
        self.receivingID = SimulationIdentifier();
        """ simulation, all simulations, a special ID, or an entity. See 5.6.5 and 5.12.4"""
        self.protocolFamily = DISProtocolFamily.SIMULATION_MANAGEMENT
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SimulationManagementFamilyPdu, self ).serialize(outputStream)
        self.originatingID.serialize(outputStream)
        self.receivingID.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SimulationManagementFamilyPdu, self).parse(inputStream)
        self.originatingID.parse(inputStream)
        self.receivingID.parse(inputStream)



class ActionResponseRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.8 Serves the same function as the Action Response PDU (see 5.6.5.8.1) but is used to acknowledge the receipt of an Action Request-R PDU."""

    def __init__(self):
        """ Initializer for ActionResponseRPdu"""
        super(ActionResponseRPdu, self).__init__()
        self.requestID = 0
        """ request ID"""
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.fixedDatumRecords = []
        """ Fixed datum records"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.ACTION_RESPONSE_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ActionResponseRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null( len(self.fixedDatumRecords));
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.fixedDatumRecords:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ActionResponseRPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatumRecords.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class FirePdu( WarfareFamilyPdu ):
    """7.3.2 Used to communicate the firing of a weapon or expendable."""

    def __init__(self):
        """ Initializer for FirePdu"""
        super(FirePdu, self).__init__()
        self.firingEntityID = EntityID();
        """ ID of the entity that shot"""
        self.targetEntityID = EntityID();
        """ ID of the entity that is being shot at"""
        self.munitionExpendibleID = EntityID();
        """ This field shall specify the entity identification of the fired munition or expendable. This field shall be represented by an Entity Identifier record (see 6.2.28)."""
        self.eventID = EventIdentifier();
        """ This field shall contain an identification generated by the firing entity to associate related firing and detonation events. This field shall be represented by an Event Identifier record (see 6.2.34)."""
        self.fireMissionIndex = 0
        """ This field shall identify the fire mission (see 5.4.3.3). This field shall be represented by a 32-bit unsigned integer."""
        self.locationInWorldCoordinates = Vector3Double();
        """ This field shall specify the location, in world coordinates, from which the munition was launched, and shall be represented by a World Coordinates record (see 6.2.97)."""
        self.descriptor = MunitionDescriptor();
        """ This field shall describe the firing or launch of a munition or expendable represented by one of the following types of Descriptor records: Munition Descriptor (6.2.20.2) or Expendable Descriptor (6.2.20.4)."""
        self.velocity = Vector3Float();
        """ This field shall specify the velocity of the fired munition at the point when the issuing simulation application intends the externally visible effects of the launch (e.g. exhaust plume or muzzle blast) to first become apparent. The velocity shall be represented in world coordinates. This field shall be represented by a Linear Velocity Vector record [see 6.2.95 item c)]."""
        self.range = 0
        """ This field shall specify the range that an entity's fire control system has assumed in computing the fire control solution. This field shall be represented by a 32-bit floating point number in meters. For systems where range is unknown or unavailable, this field shall contain a value of zero."""
        self.pduType = DisPduType.FIRE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( FirePdu, self ).serialize(outputStream)
        self.firingEntityID.serialize(outputStream)
        self.targetEntityID.serialize(outputStream)
        self.munitionExpendibleID.serialize(outputStream)
        self.eventID.serialize(outputStream)
        outputStream.write_null(self.fireMissionIndex);
        self.locationInWorldCoordinates.serialize(outputStream)
        self.descriptor.serialize(outputStream)
        self.velocity.serialize(outputStream)
        outputStream.write_null(self.range);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( FirePdu, self).parse(inputStream)
        self.firingEntityID.parse(inputStream)
        self.targetEntityID.parse(inputStream)
        self.munitionExpendibleID.parse(inputStream)
        self.eventID.parse(inputStream)
        self.fireMissionIndex = inputStream.read_null();
        self.locationInWorldCoordinates.parse(inputStream)
        self.descriptor.parse(inputStream)
        self.velocity.parse(inputStream)
        self.range = inputStream.read_null();



class DataQueryPdu( SimulationManagementFamilyPdu ):
    """Section 7.5.9. Request for data from an entity. See 5.6.5.9"""

    def __init__(self):
        """ Initializer for DataQueryPdu"""
        super(DataQueryPdu, self).__init__()
        self.requestID = 0
        """ ID of request"""
        self.timeInterval = 0
        """ time issues between issues of Data PDUs. Zero means send once only."""
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.fixedDatums = []
        """ variable length list of fixed datums"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.DATA_QUERY
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DataQueryPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.timeInterval);
        outputStream.write_null( len(self.fixedDatums));
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.fixedDatums:
            anObj.serialize(outputStream)

        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DataQueryPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.timeInterval = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatums.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class DataRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.11 Serves the same function as the Data PDU but with the addition of reliability service levels and is used in response to a Data Query-R PDU, a Data-R PDU, or a Set Data-R PDU."""

    def __init__(self):
        """ Initializer for DataRPdu"""
        super(DataRPdu, self).__init__()
        self.requestID = 0
        """ Request ID"""
        self.pad1 = 0
        """ padding"""
        self.pad2 = 0
        """ padding"""
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.fixedDatumRecords = []
        """ Fixed datum records"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.DATA_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DataRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null( len(self.fixedDatumRecords));
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.fixedDatumRecords:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DataRPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatumRecords.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class ReceiverPdu( RadioCommunicationsFamilyPdu ):
    """5.8.5 Communicates the state of a particular radio receiver. Its primary application is in communicating state information to radio network monitors, data loggers, and similar applications for use in debugging, supervision, and after-action review."""

    def __init__(self):
        """ Initializer for ReceiverPdu"""
        super(ReceiverPdu, self).__init__()
        self.header = RadioCommsHeader();
        self.padding1 = 0
        self.receivedPower = 0
        """ received power"""
        self.transmitterEntityId = EntityID();
        """ ID of transmitter"""
        self.transmitterRadioId = 0
        """ ID of transmitting radio"""
        self.pduType = DisPduType.RECEIVER
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ReceiverPdu, self ).serialize(outputStream)
        self.header.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.receivedPower);
        self.transmitterEntityId.serialize(outputStream)
        outputStream.write_null(self.transmitterRadioId);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ReceiverPdu, self).parse(inputStream)
        self.header.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.receivedPower = inputStream.read_null();
        self.transmitterEntityId.parse(inputStream)
        self.transmitterRadioId = inputStream.read_null();



class RemoveEntityRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.3 Contains the same information as found in the Remove Entity PDU with the addition of the level of reliability service to be used for the removal action being requested."""

    def __init__(self):
        """ Initializer for RemoveEntityRPdu"""
        super(RemoveEntityRPdu, self).__init__()
        self.pad1 = 0
        self.pad2 = 0
        self.requestID = 0
        """ Request ID"""
        self.pduType = DisPduType.REMOVE_ENTITY_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RemoveEntityRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RemoveEntityRPdu, self).parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.requestID = inputStream.read_null();



class LinearObjectStatePdu( SyntheticEnvironmentFamilyPdu ):
    """7.10.5 Used to communicate detailed information about the addition/modification of a synthetic environment object that is geometrically anchored to the terrain with one point and has size and orientation."""

    def __init__(self):
        """ Initializer for LinearObjectStatePdu"""
        super(LinearObjectStatePdu, self).__init__()
        self.objectID = ObjectIdentifier();
        """ Object in synthetic environment"""
        self.referencedObjectID = ObjectIdentifier();
        """ Object with which this point object is associated"""
        self.updateNumber = 0
        """ unique update number of each state transition of an object"""
        self.numberOfLinearSegments = 0
        """ number of linear segment parameters"""
        self.requesterID = SimulationAddress();
        """ requesterID"""
        self.receivingID = SimulationAddress();
        """ receiver ID"""
        self.objectType = ObjectType();
        """ Object type"""
        self.linearSegmentParameters = []
        """ Linear segment parameters"""
        self.pduType = DisPduType.LINEAR_OBJECT_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( LinearObjectStatePdu, self ).serialize(outputStream)
        self.objectID.serialize(outputStream)
        self.referencedObjectID.serialize(outputStream)
        outputStream.write_null(self.updateNumber);
        outputStream.write_null( len(self.linearSegmentParameters));
        self.requesterID.serialize(outputStream)
        self.receivingID.serialize(outputStream)
        self.objectType.serialize(outputStream)
        for anObj in self.linearSegmentParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( LinearObjectStatePdu, self).parse(inputStream)
        self.objectID.parse(inputStream)
        self.referencedObjectID.parse(inputStream)
        self.updateNumber = inputStream.read_null();
        self.numberOfLinearSegments = inputStream.read_null();
        self.requesterID.parse(inputStream)
        self.receivingID.parse(inputStream)
        self.objectType.parse(inputStream)
        for idx in range(0, self.numberOfLinearSegments):
            element = null()
            element.parse(inputStream)
            self.linearSegmentParameters.append(element)




class IntercomControlPdu( RadioCommunicationsFamilyPdu ):
    """5.8.7 Communicates the state of a particular intercom device, request an action of another intercom device, or respond to an action request."""

    def __init__(self):
        """ Initializer for IntercomControlPdu"""
        super(IntercomControlPdu, self).__init__()
        self.communicationsChannelType = 0
        """ control type"""
        self.sourceEntityID = EntityID();
        """ Source entity ID, this can also be ObjectIdentifier or UnattachedIdentifier"""
        self.sourceIntercomNumber = 0
        """ The specific intercom device being simulated within an entity."""
        self.sourceLineID = 0
        """ Line number to which the intercom control refers"""
        self.transmitPriority = 0
        """ priority of this message relative to transmissons from other intercom devices"""
        self.masterIntercomReferenceID = EntityID();
        """ eid of the entity that has created this intercom channel, same comments as sourceEntityId"""
        self.masterIntercomNumber = 0
        """ specific intercom device that has created this intercom channel"""
        self.masterChannelID = 0
        self.intercomParametersLength = 0
        """ number of intercom parameters"""
        self.intercomParameters = []
        self.pduType = DisPduType.INTERCOM_CONTROL
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( IntercomControlPdu, self ).serialize(outputStream)
        outputStream.write_null(self.communicationsChannelType);
        self.sourceEntityID.serialize(outputStream)
        outputStream.write_null(self.sourceIntercomNumber);
        outputStream.write_null(self.sourceLineID);
        outputStream.write_null(self.transmitPriority);
        self.masterIntercomReferenceID.serialize(outputStream)
        outputStream.write_null(self.masterIntercomNumber);
        outputStream.write_null(self.masterChannelID);
        outputStream.write_null( len(self.intercomParameters));
        for anObj in self.intercomParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IntercomControlPdu, self).parse(inputStream)
        self.communicationsChannelType = inputStream.read_null();
        self.sourceEntityID.parse(inputStream)
        self.sourceIntercomNumber = inputStream.read_null();
        self.sourceLineID = inputStream.read_null();
        self.transmitPriority = inputStream.read_null();
        self.masterIntercomReferenceID.parse(inputStream)
        self.masterIntercomNumber = inputStream.read_null();
        self.masterChannelID = inputStream.read_null();
        self.intercomParametersLength = inputStream.read_null();
        for idx in range(0, self.intercomParametersLength):
            element = null()
            element.parse(inputStream)
            self.intercomParameters.append(element)




class SignalPdu( RadioCommunicationsFamilyPdu ):
    """5.8.4 Conveys the audio or digital data carried by the simulated radio or intercom transmission."""

    def __init__(self):
        """ Initializer for SignalPdu"""
        super(SignalPdu, self).__init__()
        self.header = RadioCommsHeader();
        self.encodingScheme = 0
        """ encoding scheme used, and enumeration"""
        self.sampleRate = 0
        """ sample rate"""
        self.dataLength = 0
        """ length of data in bits"""
        self.samples = 0
        """ number of samples"""
        self.data =  []
        """ list of eight bit values"""
        self.pduType = DisPduType.SIGNAL
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SignalPdu, self ).serialize(outputStream)
        self.header.serialize(outputStream)
        outputStream.write_null(self.encodingScheme);
        outputStream.write_null(self.sampleRate);
        outputStream.write_null(self.dataLength);
        outputStream.write_null(self.samples);
        for idx in range(0, 0):
            outputStream.write_null( self.data[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SignalPdu, self).parse(inputStream)
        self.header.parse(inputStream)
        self.encodingScheme = inputStream.read_null();
        self.sampleRate = inputStream.read_null();
        self.dataLength = inputStream.read_null();
        self.samples = inputStream.read_null();
        self.data = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.data[  idx  ] = val




class EntityInformationInteractionFamilyPdu( PduBase ):
    """Section 5.3.3. Common superclass for EntityState, Collision, collision-elastic, and entity state update PDUs."""

    def __init__(self):
        """ Initializer for EntityInformationInteractionFamilyPdu"""
        super(EntityInformationInteractionFamilyPdu, self).__init__()
        self.protocolFamily = DISProtocolFamily.ENTITY_INFORMATION_INTERACTION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EntityInformationInteractionFamilyPdu, self ).serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EntityInformationInteractionFamilyPdu, self).parse(inputStream)



class InformationOperationsReportPdu( InformationOperationsFamilyPdu ):
    """5.13.4.1 Used to communicate the effects of an IO attack on one or more target entities."""

    def __init__(self):
        """ Initializer for InformationOperationsReportPdu"""
        super(InformationOperationsReportPdu, self).__init__()
        self.padding1 = 0
        self.ioAttackerID = EntityID();
        self.ioPrimaryTargetID = EntityID();
        self.padding2 = 0
        self.padding3 = 0
        self.numberOfIORecords = 0
        self.ioRecords = []
        self.pduType = DisPduType.INFORMATION_OPERATIONS_REPORT
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( InformationOperationsReportPdu, self ).serialize(outputStream)
        outputStream.write_null(self.padding1);
        self.ioAttackerID.serialize(outputStream)
        self.ioPrimaryTargetID.serialize(outputStream)
        outputStream.write_null(self.padding2);
        outputStream.write_null(self.padding3);
        outputStream.write_null( len(self.ioRecords));
        for anObj in self.ioRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( InformationOperationsReportPdu, self).parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.ioAttackerID.parse(inputStream)
        self.ioPrimaryTargetID.parse(inputStream)
        self.padding2 = inputStream.read_null();
        self.padding3 = inputStream.read_null();
        self.numberOfIORecords = inputStream.read_null();
        for idx in range(0, self.numberOfIORecords):
            element = null()
            element.parse(inputStream)
            self.ioRecords.append(element)




class StartResumeRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.4 Serves the same function as the Start/Resume PDU but with the addition of reliability service levels"""

    def __init__(self):
        """ Initializer for StartResumeRPdu"""
        super(StartResumeRPdu, self).__init__()
        self.realWorldTime = ClockTime();
        """ time in real world for this operation to happen"""
        self.simulationTime = ClockTime();
        """ time in simulation for the simulation to resume"""
        self.pad1 = 0
        self.pad2 = 0
        self.requestID = 0
        """ Request ID"""
        self.pduType = DisPduType.START_RESUME_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( StartResumeRPdu, self ).serialize(outputStream)
        self.realWorldTime.serialize(outputStream)
        self.simulationTime.serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( StartResumeRPdu, self).parse(inputStream)
        self.realWorldTime.parse(inputStream)
        self.simulationTime.parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.requestID = inputStream.read_null();



class CollisionElasticPdu( EntityInformationInteractionFamilyPdu ):
    """7.2.4 Information about elastic collisions in a DIS exercise shall be communicated using a Collision-Elastic PDU. See 5.3.4."""

    def __init__(self):
        """ Initializer for CollisionElasticPdu"""
        super(CollisionElasticPdu, self).__init__()
        self.issuingEntityID = EntityID();
        """ This field shall identify the entity that is issuing the PDU and shall be represented by an Entity Identifier record (see 6.2.28)"""
        self.collidingEntityID = EntityID();
        """ This field shall identify the entity that has collided with the issuing entity. This field shall be a valid identifier of an entity or server capable of responding to the receipt of this Collision-Elastic PDU. This field shall be represented by an Entity Identifier record (see 6.2.28)."""
        self.collisionEventID = EventIdentifier();
        """ This field shall contain an identification generated by the issuing simulation application to associate related collision events. This field shall be represented by an Event Identifier record (see 6.2.34)."""
        self.pad = 0
        """ some padding"""
        self.contactVelocity = Vector3Float();
        """ This field shall contain the velocity at the time the collision is detected at the point the collision is detected. The velocity shall be represented in world coordinates. This field shall be represented by the Linear Velocity Vector record [see 6.2.95 item c)]"""
        self.mass = 0
        """ This field shall contain the mass of the issuing entity and shall be represented by a 32-bit floating point number representing kilograms"""
        self.locationOfImpact = Vector3Float();
        """ This field shall specify the location of the collision with respect to the entity with which the issuing entity collided. This field shall be represented by an Entity Coordinate Vector record [see 6.2.95 item a)]."""
        self.collisionIntermediateResultXX = 0
        """ These six records represent the six independent components of a positive semi-definite matrix formed by pre-multiplying and post-multiplying the tensor of inertia, by the anti-symmetric matrix generated by the moment arm, and shall be represented by 32-bit floating point numbers (see 5.3.4.4)"""
        self.collisionIntermediateResultXY = 0
        """ tensor values"""
        self.collisionIntermediateResultXZ = 0
        """ tensor values"""
        self.collisionIntermediateResultYY = 0
        """ tensor values"""
        self.collisionIntermediateResultYZ = 0
        """ tensor values"""
        self.collisionIntermediateResultZZ = 0
        """ tensor values"""
        self.unitSurfaceNormal = Vector3Float();
        """ This record shall represent the normal vector to the surface at the point of collision detection. The surface normal shall be represented in world coordinates. This field shall be represented by an Entity Coordinate Vector record [see 6.2.95 item a)]."""
        self.coefficientOfRestitution = 0
        """ This field shall represent the degree to which energy is conserved in a collision and shall be represented by a 32-bit floating point number. In addition, it represents a free parameter by which simulation application developers may "tune" their collision interactions."""
        self.pduType = DisPduType.COLLISION_ELASTIC
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( CollisionElasticPdu, self ).serialize(outputStream)
        self.issuingEntityID.serialize(outputStream)
        self.collidingEntityID.serialize(outputStream)
        self.collisionEventID.serialize(outputStream)
        outputStream.write_null(self.pad);
        self.contactVelocity.serialize(outputStream)
        outputStream.write_null(self.mass);
        self.locationOfImpact.serialize(outputStream)
        outputStream.write_null(self.collisionIntermediateResultXX);
        outputStream.write_null(self.collisionIntermediateResultXY);
        outputStream.write_null(self.collisionIntermediateResultXZ);
        outputStream.write_null(self.collisionIntermediateResultYY);
        outputStream.write_null(self.collisionIntermediateResultYZ);
        outputStream.write_null(self.collisionIntermediateResultZZ);
        self.unitSurfaceNormal.serialize(outputStream)
        outputStream.write_null(self.coefficientOfRestitution);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( CollisionElasticPdu, self).parse(inputStream)
        self.issuingEntityID.parse(inputStream)
        self.collidingEntityID.parse(inputStream)
        self.collisionEventID.parse(inputStream)
        self.pad = inputStream.read_null();
        self.contactVelocity.parse(inputStream)
        self.mass = inputStream.read_null();
        self.locationOfImpact.parse(inputStream)
        self.collisionIntermediateResultXX = inputStream.read_null();
        self.collisionIntermediateResultXY = inputStream.read_null();
        self.collisionIntermediateResultXZ = inputStream.read_null();
        self.collisionIntermediateResultYY = inputStream.read_null();
        self.collisionIntermediateResultYZ = inputStream.read_null();
        self.collisionIntermediateResultZZ = inputStream.read_null();
        self.unitSurfaceNormal.parse(inputStream)
        self.coefficientOfRestitution = inputStream.read_null();



class TSPIPdu( LiveEntityFamilyPdu ):
    """9.4.2 The Time Space Position Information (TSPI) PDU shall communicate information about the LE’s state vector."""

    def __init__(self):
        """ Initializer for TSPIPdu"""
        super(TSPIPdu, self).__init__()
        self.liveEntityId = EntityID();
        self.TSPIFlag = 0
        """ bit field"""
        self.entityLocation = LiveEntityRelativeWorldCoordinates();
        self.entityLinearVelocity = LiveEntityLinearVelocity();
        self.entityOrientation = LiveEntityOrientation();
        self.positionError = LiveEntityPositionError();
        self.orientationError = LiveEntityOrientationError();
        self.deadReckoningParameters = LiveDeadReckoningParameters();
        self.measuredSpeed = 0
        self.systemSpecificDataLength = 0
        self.systemSpecificData =  []
        self.pduType = DisPduType.TIME_SPACE_POSITION_INFORMATION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( TSPIPdu, self ).serialize(outputStream)
        self.liveEntityId.serialize(outputStream)
        outputStream.write_null(self.TSPIFlag);
        self.entityLocation.serialize(outputStream)
        self.entityLinearVelocity.serialize(outputStream)
        self.entityOrientation.serialize(outputStream)
        self.positionError.serialize(outputStream)
        self.orientationError.serialize(outputStream)
        self.deadReckoningParameters.serialize(outputStream)
        outputStream.write_null(self.measuredSpeed);
        outputStream.write_null(self.systemSpecificDataLength);
        for idx in range(0, 0):
            outputStream.write_null( self.systemSpecificData[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( TSPIPdu, self).parse(inputStream)
        self.liveEntityId.parse(inputStream)
        self.TSPIFlag = inputStream.read_null();
        self.entityLocation.parse(inputStream)
        self.entityLinearVelocity.parse(inputStream)
        self.entityOrientation.parse(inputStream)
        self.positionError.parse(inputStream)
        self.orientationError.parse(inputStream)
        self.deadReckoningParameters.parse(inputStream)
        self.measuredSpeed = inputStream.read_null();
        self.systemSpecificDataLength = inputStream.read_null();
        self.systemSpecificData = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.systemSpecificData[  idx  ] = val




class DesignatorPdu( DistributedEmissionsRegenerationFamilyPdu ):
    """7.6.3 Handles designating operations. See 5.3.7.2."""

    def __init__(self):
        """ Initializer for DesignatorPdu"""
        super(DesignatorPdu, self).__init__()
        self.designatingEntityID = EntityID();
        """ ID of the entity designating"""
        self.designatedEntityID = EntityID();
        """ ID of the entity being designated"""
        self.designatorPower = 0
        """ This field shall identify the designator output power in watts"""
        self.designatorWavelength = 0
        """ This field shall identify the designator wavelength in units of microns"""
        self.designatorSpotWrtDesignated = Vector3Float();
        """ designtor spot wrt the designated entity"""
        self.designatorSpotLocation = Vector3Double();
        """ designtor spot wrt the designated entity"""
        self.padding1 = 0
        """ padding"""
        self.padding2 = 0
        """ padding"""
        self.entityLinearAcceleration = Vector3Float();
        """ linear accelleration of entity"""
        self.pduType = DisPduType.DESIGNATOR
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DesignatorPdu, self ).serialize(outputStream)
        self.designatingEntityID.serialize(outputStream)
        self.designatedEntityID.serialize(outputStream)
        outputStream.write_null(self.designatorPower);
        outputStream.write_null(self.designatorWavelength);
        self.designatorSpotWrtDesignated.serialize(outputStream)
        self.designatorSpotLocation.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        self.entityLinearAcceleration.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DesignatorPdu, self).parse(inputStream)
        self.designatingEntityID.parse(inputStream)
        self.designatedEntityID.parse(inputStream)
        self.designatorPower = inputStream.read_null();
        self.designatorWavelength = inputStream.read_null();
        self.designatorSpotWrtDesignated.parse(inputStream)
        self.designatorSpotLocation.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.entityLinearAcceleration.parse(inputStream)



class EntityStatePdu( EntityInformationInteractionFamilyPdu ):
    """ 7.2.2. Represents the position and state of one entity in the world. See 5.3.2."""

    def __init__(self):
        """ Initializer for EntityStatePdu"""
        super(EntityStatePdu, self).__init__()
        self.entityID = EntityID();
        """ Unique ID for an entity that is tied to this state information"""
        self.numberOfVariableParameters = 0
        """ How many variable parameters are in the variable length list. In earlier versions of DIS these were known as articulation parameters"""
        self.entityType = EntityType();
        """ Describes the type of entity in the world"""
        self.alternativeEntityType = EntityType();
        self.entityLinearVelocity = Vector3Float();
        """ Describes the speed of the entity in the world"""
        self.entityLocation = Vector3Double();
        """ describes the location of the entity in the world"""
        self.entityOrientation = EulerAngles();
        """ describes the orientation of the entity, in euler angles with units of radians"""
        self.entityAppearance = 0
        """ a series of bit flags that are used to help draw the entity, such as smoking, on fire, etc."""
        self.deadReckoningParameters = DeadReckoningParameters();
        """ parameters used for dead reckoning"""
        self.marking = EntityMarking();
        """ 11 characters that can be used for entity identification, debugging, or to draw unique strings on the side of entities in the world"""
        self.variableParameters = []
        """ variable length list of variable parameters. In earlier DIS versions this was articulation parameters."""
        self.pduType = DisPduType.ENTITY_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EntityStatePdu, self ).serialize(outputStream)
        self.entityID.serialize(outputStream)
        outputStream.write_null( len(self.variableParameters));
        self.entityType.serialize(outputStream)
        self.alternativeEntityType.serialize(outputStream)
        self.entityLinearVelocity.serialize(outputStream)
        self.entityLocation.serialize(outputStream)
        self.entityOrientation.serialize(outputStream)
        outputStream.write_null(self.entityAppearance);
        self.deadReckoningParameters.serialize(outputStream)
        self.marking.serialize(outputStream)
        for anObj in self.variableParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EntityStatePdu, self).parse(inputStream)
        self.entityID.parse(inputStream)
        self.numberOfVariableParameters = inputStream.read_null();
        self.entityType.parse(inputStream)
        self.alternativeEntityType.parse(inputStream)
        self.entityLinearVelocity.parse(inputStream)
        self.entityLocation.parse(inputStream)
        self.entityOrientation.parse(inputStream)
        self.entityAppearance = inputStream.read_null();
        self.deadReckoningParameters.parse(inputStream)
        self.marking.parse(inputStream)
        for idx in range(0, self.numberOfVariableParameters):
            element = null()
            element.parse(inputStream)
            self.variableParameters.append(element)




class ArticulatedPartsPdu( LiveEntityFamilyPdu ):
    """9.4.4 Communicate information about an entity’s articulated and attached parts."""

    def __init__(self):
        """ Initializer for ArticulatedPartsPdu"""
        super(ArticulatedPartsPdu, self).__init__()
        self.liveEntityId = EntityID();
        self.numberOfParameterRecords = 0
        self.variableParameters = []
        self.pduType = DisPduType.ARTICULATED_PARTS
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ArticulatedPartsPdu, self ).serialize(outputStream)
        self.liveEntityId.serialize(outputStream)
        outputStream.write_null( len(self.variableParameters));
        for anObj in self.variableParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ArticulatedPartsPdu, self).parse(inputStream)
        self.liveEntityId.parse(inputStream)
        self.numberOfParameterRecords = inputStream.read_null();
        for idx in range(0, self.numberOfParameterRecords):
            element = null()
            element.parse(inputStream)
            self.variableParameters.append(element)




class SetDataRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.10 Serves the same function as the Set Data PDU but with the addition of reliability service levels."""

    def __init__(self):
        """ Initializer for SetDataRPdu"""
        super(SetDataRPdu, self).__init__()
        self.pad1 = 0
        """ padding"""
        self.pad2 = 0
        """ padding"""
        self.requestID = 0
        """ request ID"""
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.fixedDatumRecords = []
        """ Fixed datum records"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.SET_DATA_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SetDataRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.requestID);
        outputStream.write_null( len(self.fixedDatumRecords));
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.fixedDatumRecords:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SetDataRPdu, self).parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.requestID = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatumRecords.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class CommentRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.13 Serves the same function as the Comment PDU."""

    def __init__(self):
        """ Initializer for CommentRPdu"""
        super(CommentRPdu, self).__init__()
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count, not used in this Pdu"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.COMMENT_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( CommentRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.numberOfFixedDatumRecords);
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( CommentRPdu, self).parse(inputStream)
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class TransmitterPdu( RadioCommunicationsFamilyPdu ):
    """5.8.3 Communicates the state of a particular radio transmitter or simple intercom."""

    def __init__(self):
        """ Initializer for TransmitterPdu"""
        super(TransmitterPdu, self).__init__()
        self.header = RadioCommsHeader();
        self.radioNumber = 0
        """ particular radio within an entity"""
        self.radioEntityType = RadioType();
        """ Type of radio"""
        self.variableTransmitterParameterCount = 0
        """ count field"""
        self.antennaLocation = Vector3Double();
        """ Location of antenna"""
        self.relativeAntennaLocation = Vector3Float();
        """ relative location of antenna"""
        self.antennaPatternCount = 0
        """ atenna pattern length"""
        self.frequency = 0
        """ frequency"""
        self.transmitFrequencyBandwidth = 0
        """ transmit frequency Bandwidth"""
        self.power = 0
        """ transmission power"""
        self.modulationType = ModulationType();
        """ modulation"""
        self.cryptoKeyId = 0
        """ crypto system key identifer"""
        self.modulationParameterCount = 0
        """ how many modulation parameters we have"""
        self.padding1 = 0
        self.padding2 = 0
        self.modulationParametersList = []
        """ variable length list of modulation parameters"""
        self.antennaPatternList = []
        """ variable length list of antenna pattern records"""
        self.pduType = DisPduType.TRANSMITTER
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( TransmitterPdu, self ).serialize(outputStream)
        self.header.serialize(outputStream)
        outputStream.write_null(self.radioNumber);
        self.radioEntityType.serialize(outputStream)
        outputStream.write_null(self.variableTransmitterParameterCount);
        self.antennaLocation.serialize(outputStream)
        self.relativeAntennaLocation.serialize(outputStream)
        outputStream.write_null( len(self.antennaPatternList));
        outputStream.write_null(self.frequency);
        outputStream.write_null(self.transmitFrequencyBandwidth);
        outputStream.write_null(self.power);
        self.modulationType.serialize(outputStream)
        outputStream.write_null(self.cryptoKeyId);
        outputStream.write_null( len(self.modulationParametersList));
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        for anObj in self.modulationParametersList:
            anObj.serialize(outputStream)

        for anObj in self.antennaPatternList:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( TransmitterPdu, self).parse(inputStream)
        self.header.parse(inputStream)
        self.radioNumber = inputStream.read_null();
        self.radioEntityType.parse(inputStream)
        self.variableTransmitterParameterCount = inputStream.read_null();
        self.antennaLocation.parse(inputStream)
        self.relativeAntennaLocation.parse(inputStream)
        self.antennaPatternCount = inputStream.read_null();
        self.frequency = inputStream.read_null();
        self.transmitFrequencyBandwidth = inputStream.read_null();
        self.power = inputStream.read_null();
        self.modulationType.parse(inputStream)
        self.cryptoKeyId = inputStream.read_null();
        self.modulationParameterCount = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        for idx in range(0, self.modulationParameterCount):
            element = null()
            element.parse(inputStream)
            self.modulationParametersList.append(element)

        for idx in range(0, self.antennaPatternCount):
            element = null()
            element.parse(inputStream)
            self.antennaPatternList.append(element)




class SetRecordRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.15 Used to set or change certain parameter values. These parameter values are contained within a record format as compared to the datum format used in the Set Data-R PDU."""

    def __init__(self):
        """ Initializer for SetRecordRPdu"""
        super(SetRecordRPdu, self).__init__()
        self.requestID = 0
        """ request ID"""
        self.pad1 = 0
        self.pad2 = 0
        self.pad3 = 0
        self.numberOfRecordSets = 0
        """ Number of record sets in list"""
        self.recordSets = []
        """ record sets"""
        self.pduType = DisPduType.SET_RECORD_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SetRecordRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.pad3);
        outputStream.write_null( len(self.recordSets));
        for anObj in self.recordSets:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SetRecordRPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.pad3 = inputStream.read_null();
        self.numberOfRecordSets = inputStream.read_null();
        for idx in range(0, self.numberOfRecordSets):
            element = null()
            element.parse(inputStream)
            self.recordSets.append(element)




class LEDetonationPdu( LiveEntityFamilyPdu ):
    """9.4.6 Communicate information associated with the impact or detonation of a munition."""

    def __init__(self):
        """ Initializer for LEDetonationPdu"""
        super(LEDetonationPdu, self).__init__()
        self.firingLiveEntityId = EntityID();
        self.detonationFlag1 = 0
        self.detonationFlag2 = 0
        self.targetLiveEntityId = EntityID();
        self.munitionLiveEntityId = EntityID();
        self.eventId = EventIdentifier();
        self.worldLocation = LiveEntityRelativeWorldCoordinates();
        self.velocity = LiveEntityLinearVelocity();
        self.munitionOrientation = LiveEntityOrientation16();
        """ spec error? 16-bit fields vs. 8-bit in TspiPdu?"""
        self.munitionDescriptor = MunitionDescriptor();
        self.entityLocation = LiveEntityLinearVelocity();
        self.detonationResult = 0
        self.pduType = DisPduType.LIVE_ENTITY_DETONATION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( LEDetonationPdu, self ).serialize(outputStream)
        self.firingLiveEntityId.serialize(outputStream)
        outputStream.write_null(self.detonationFlag1);
        outputStream.write_null(self.detonationFlag2);
        self.targetLiveEntityId.serialize(outputStream)
        self.munitionLiveEntityId.serialize(outputStream)
        self.eventId.serialize(outputStream)
        self.worldLocation.serialize(outputStream)
        self.velocity.serialize(outputStream)
        self.munitionOrientation.serialize(outputStream)
        self.munitionDescriptor.serialize(outputStream)
        self.entityLocation.serialize(outputStream)
        outputStream.write_null(self.detonationResult);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( LEDetonationPdu, self).parse(inputStream)
        self.firingLiveEntityId.parse(inputStream)
        self.detonationFlag1 = inputStream.read_null();
        self.detonationFlag2 = inputStream.read_null();
        self.targetLiveEntityId.parse(inputStream)
        self.munitionLiveEntityId.parse(inputStream)
        self.eventId.parse(inputStream)
        self.worldLocation.parse(inputStream)
        self.velocity.parse(inputStream)
        self.munitionOrientation.parse(inputStream)
        self.munitionDescriptor.parse(inputStream)
        self.entityLocation.parse(inputStream)
        self.detonationResult = inputStream.read_null();



class EnvironmentalProcessPdu( SyntheticEnvironmentFamilyPdu ):
    """7.10.2 Used to communicate information about environmental effects and processes."""

    def __init__(self):
        """ Initializer for EnvironmentalProcessPdu"""
        super(EnvironmentalProcessPdu, self).__init__()
        self.environementalProcessID = ObjectIdentifier();
        """ Environmental process ID"""
        self.environmentType = EntityType();
        """ Environment type"""
        self.numberOfEnvironmentRecords = 0
        """ number of environment records """
        self.sequenceNumber = 0
        """ PDU sequence number for the environmental process if pdu sequencing required"""
        self.environmentRecords = []
        """ environmemt records"""
        self.pduType = DisPduType.ENVIRONMENTAL_PROCESS
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EnvironmentalProcessPdu, self ).serialize(outputStream)
        self.environementalProcessID.serialize(outputStream)
        self.environmentType.serialize(outputStream)
        outputStream.write_null( len(self.environmentRecords));
        outputStream.write_null(self.sequenceNumber);
        for anObj in self.environmentRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EnvironmentalProcessPdu, self).parse(inputStream)
        self.environementalProcessID.parse(inputStream)
        self.environmentType.parse(inputStream)
        self.numberOfEnvironmentRecords = inputStream.read_null();
        self.sequenceNumber = inputStream.read_null();
        for idx in range(0, self.numberOfEnvironmentRecords):
            element = null()
            element.parse(inputStream)
            self.environmentRecords.append(element)




class ElectromagneticEmissionPdu( DistributedEmissionsRegenerationFamilyPdu ):
    """7.6.2 Communicate active electromagnetic emissions, including radar and radar-related electronic warfare (e.g., jamming). Exceptions include IFF interrogations and replies, navigation aids, voice, beacon and data radio communications, directed energy weapons, and laser ranging and designation systems, which are handled by other PDUs. Section 5.3.7.1."""

    def __init__(self):
        """ Initializer for ElectromagneticEmissionPdu"""
        super(ElectromagneticEmissionPdu, self).__init__()
        self.emittingEntityID = EntityID();
        """ ID of the entity emitting"""
        self.eventID = EventIdentifier();
        """ ID of event"""
        self.numberOfSystems = 0
        """ This field shall specify the number of emission systems being described in the current PDU."""
        self.paddingForEmissionsPdu = 0
        """ padding"""
        self.systems = []
        """ Electronic emmissions systems"""
        self.pduType = DisPduType.ELECTROMAGNETIC_EMISSION
        """ initialize value """
        self.paddingForEmissionsPdu = 0
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ElectromagneticEmissionPdu, self ).serialize(outputStream)
        self.emittingEntityID.serialize(outputStream)
        self.eventID.serialize(outputStream)
        outputStream.write_null( len(self.systems));
        outputStream.write_null(self.paddingForEmissionsPdu);
        for anObj in self.systems:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ElectromagneticEmissionPdu, self).parse(inputStream)
        self.emittingEntityID.parse(inputStream)
        self.eventID.parse(inputStream)
        self.numberOfSystems = inputStream.read_null();
        self.paddingForEmissionsPdu = inputStream.read_null();
        for idx in range(0, self.numberOfSystems):
            element = null()
            element.parse(inputStream)
            self.systems.append(element)




class IFFLayer2Pdu( IFFPdu ):
    """Section 5.3.7.4.2 When present, layer 2 should follow layer 1 and have the following fields. This requires manual cleanup.            the beamData attribute semantics are used in multiple ways. UNFINSISHED"""

    def __init__(self):
        """ Initializer for IFFLayer2Pdu"""
        super(IFFLayer2Pdu, self).__init__()
        self.layerHeader = LayerHeader();
        """ layer header"""
        self.beamData = BeamData();
        """ beam data"""
        self.secondaryOpParameter1 = 0
        self.secondaryOpParameter2 = 0
        self.numberOfParameters = 0
        self.fundamentalIFFParameters = []
        """ variable length list of fundamental parameters."""

    def serialize(self, outputStream):
        """serialize the class """
        super( IFFLayer2Pdu, self ).serialize(outputStream)
        self.layerHeader.serialize(outputStream)
        self.beamData.serialize(outputStream)
        outputStream.write_null(self.secondaryOpParameter1);
        outputStream.write_null(self.secondaryOpParameter2);
        outputStream.write_null( len(self.fundamentalIFFParameters));
        for anObj in self.fundamentalIFFParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IFFLayer2Pdu, self).parse(inputStream)
        self.layerHeader.parse(inputStream)
        self.beamData.parse(inputStream)
        self.secondaryOpParameter1 = inputStream.read_null();
        self.secondaryOpParameter2 = inputStream.read_null();
        self.numberOfParameters = inputStream.read_null();
        for idx in range(0, self.numberOfParameters):
            element = null()
            element.parse(inputStream)
            self.fundamentalIFFParameters.append(element)




class AggregateStatePdu( EntityManagementFamilyPdu ):
    """5.9.2.2 The Aggregate State PDU shall be used to communicate the state and other pertinent information about an aggregated unit."""

    def __init__(self):
        """ Initializer for AggregateStatePdu"""
        super(AggregateStatePdu, self).__init__()
        self.aggregateID = AggregateIdentifier();
        """ ID of aggregated entities"""
        self.aggregateType = AggregateType();
        """ entity type of the aggregated entities"""
        self.aggregateMarking = AggregateMarking();
        """ marking for aggregate; first char is charset type, rest is char data"""
        self.dimensions = Vector3Float();
        """ dimensions of bounding box for the aggregated entities, origin at the center of mass"""
        self.orientation = Vector3Float();
        """ orientation of the bounding box"""
        self.centerOfMass = Vector3Double();
        """ center of mass of the aggregation"""
        self.velocity = Vector3Float();
        """ velocity of aggregation"""
        self.numberOfDisAggregates = 0
        """ number of aggregates"""
        self.numberOfDisEntities = 0
        """ number of entities"""
        self.numberOfSilentAggregateTypes = 0
        """ number of silent aggregate types"""
        self.numberOfSilentEntityTypes = 0
        """ Number of silent entity types, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.aggregateIDList = []
        """ aggregates  list"""
        self.entityIDList = []
        """ entity ID list"""
        self.silentAggregateSystemList = []
        """ silent entity types"""
        self.silentEntitySystemList = []
        """ silent entity types"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.variableDatumList = []
        """ variableDatums"""
        self.pduType = DisPduType.AGGREGATE_STATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( AggregateStatePdu, self ).serialize(outputStream)
        self.aggregateID.serialize(outputStream)
        self.aggregateType.serialize(outputStream)
        self.aggregateMarking.serialize(outputStream)
        self.dimensions.serialize(outputStream)
        self.orientation.serialize(outputStream)
        self.centerOfMass.serialize(outputStream)
        self.velocity.serialize(outputStream)
        outputStream.write_null( len(self.aggregateIDList));
        outputStream.write_null( len(self.entityIDList));
        outputStream.write_null( len(self.silentAggregateSystemList));
        outputStream.write_null( len(self.silentEntitySystemList));
        for anObj in self.aggregateIDList:
            anObj.serialize(outputStream)

        for anObj in self.entityIDList:
            anObj.serialize(outputStream)

        for anObj in self.silentAggregateSystemList:
            anObj.serialize(outputStream)

        for anObj in self.silentEntitySystemList:
            anObj.serialize(outputStream)

        outputStream.write_null( len(self.variableDatumList));
        for anObj in self.variableDatumList:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( AggregateStatePdu, self).parse(inputStream)
        self.aggregateID.parse(inputStream)
        self.aggregateType.parse(inputStream)
        self.aggregateMarking.parse(inputStream)
        self.dimensions.parse(inputStream)
        self.orientation.parse(inputStream)
        self.centerOfMass.parse(inputStream)
        self.velocity.parse(inputStream)
        self.numberOfDisAggregates = inputStream.read_null();
        self.numberOfDisEntities = inputStream.read_null();
        self.numberOfSilentAggregateTypes = inputStream.read_null();
        self.numberOfSilentEntityTypes = inputStream.read_null();
        for idx in range(0, self.numberOfDisAggregates):
            element = null()
            element.parse(inputStream)
            self.aggregateIDList.append(element)

        for idx in range(0, self.numberOfDisEntities):
            element = null()
            element.parse(inputStream)
            self.entityIDList.append(element)

        for idx in range(0, self.numberOfSilentAggregateTypes):
            element = null()
            element.parse(inputStream)
            self.silentAggregateSystemList.append(element)

        for idx in range(0, self.numberOfSilentEntityTypes):
            element = null()
            element.parse(inputStream)
            self.silentEntitySystemList.append(element)

        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumList.append(element)




class ActionRequestRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.7 Serves the same function as the Action Request PDU but with the addition of reliability service levels."""

    def __init__(self):
        """ Initializer for ActionRequestRPdu"""
        super(ActionRequestRPdu, self).__init__()
        self.pad1 = 0
        """ padding"""
        self.pad2 = 0
        """ padding"""
        self.requestID = 0
        """ request ID"""
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.fixedDatumRecords = []
        """ Fixed datum records"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.ACTION_REQUEST_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ActionRequestRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.requestID);
        outputStream.write_null( len(self.fixedDatumRecords));
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.fixedDatumRecords:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ActionRequestRPdu, self).parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.requestID = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatumRecords.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class DataQueryRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.9 Serves the same function as the Data Query PDU but with the addition of reliability service levels"""

    def __init__(self):
        """ Initializer for DataQueryRPdu"""
        super(DataQueryRPdu, self).__init__()
        self.pad1 = 0
        """ padding"""
        self.pad2 = 0
        """ padding"""
        self.requestID = 0
        """ request ID"""
        self.timeInterval = 0
        """ time interval between issuing data query PDUs"""
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.fixedDatumRecords = []
        """ Fixed datum records"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.DATA_QUERY_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DataQueryRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.pad2);
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.timeInterval);
        outputStream.write_null( len(self.fixedDatumRecords));
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.fixedDatumRecords:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DataQueryRPdu, self).parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.pad2 = inputStream.read_null();
        self.requestID = inputStream.read_null();
        self.timeInterval = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatumRecords.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class EntityDamageStatusPdu( WarfareFamilyPdu ):
    """7.3.5 Used to communicate detailed damage information sustained by an entity regardless of the source of the damage."""

    def __init__(self):
        """ Initializer for EntityDamageStatusPdu"""
        super(EntityDamageStatusPdu, self).__init__()
        self.damagedEntityID = EntityID();
        """ Field shall identify the damaged entity (see 6.2.28), Section 7.3.4"""
        self.padding1 = 0
        self.padding2 = 0
        self.numberOfDamageDescription = 0
        """ field shall specify the number of Damage Description records, Section 7.3.5"""
        self.damageDescriptionRecords = []
        """ Fields shall contain one or more Damage Description records (see 6.2.17) and may contain other Standard Variable records, Section 7.3.5"""
        self.pduType = DisPduType.ENTITY_DAMAGE_STATUS
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EntityDamageStatusPdu, self ).serialize(outputStream)
        self.damagedEntityID.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        outputStream.write_null( len(self.damageDescriptionRecords));
        for anObj in self.damageDescriptionRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EntityDamageStatusPdu, self).parse(inputStream)
        self.damagedEntityID.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.numberOfDamageDescription = inputStream.read_null();
        for idx in range(0, self.numberOfDamageDescription):
            element = null()
            element.parse(inputStream)
            self.damageDescriptionRecords.append(element)




class StopFreezeRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.5 Serves the same function as the Stop/Freeze PDU (see 5.6.5.5.1) but with the addition of reliability service levels."""

    def __init__(self):
        """ Initializer for StopFreezeRPdu"""
        super(StopFreezeRPdu, self).__init__()
        self.realWorldTime = ClockTime();
        """ time in real world for this operation to happen"""
        self.pad1 = 0
        """ padding"""
        self.requestID = 0
        """ Request ID"""
        self.pduType = DisPduType.STOP_FREEZE_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( StopFreezeRPdu, self ).serialize(outputStream)
        self.realWorldTime.serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( StopFreezeRPdu, self).parse(inputStream)
        self.realWorldTime.parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.requestID = inputStream.read_null();



class TransferOwnershipPdu( EntityManagementFamilyPdu ):
    """ Information initiating the dyanic allocation and control of simulation entities between two simulation applications."""

    def __init__(self):
        """ Initializer for TransferOwnershipPdu"""
        super(TransferOwnershipPdu, self).__init__()
        self.originatingEntityID = EntityID();
        """ ID of entity originating request"""
        self.receivingEntityID = EntityID();
        """ ID of entity receiving request"""
        self.requestID = 0
        """ ID of request"""
        self.transferEntityID = EntityID();
        """ The entity for which control is being requested to transfer"""
        self.recordSets = RecordSpecification();
        self.pduType = DisPduType.TRANSFER_OWNERSHIP
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( TransferOwnershipPdu, self ).serialize(outputStream)
        self.originatingEntityID.serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        outputStream.write_null(self.requestID);
        self.transferEntityID.serialize(outputStream)
        self.recordSets.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( TransferOwnershipPdu, self).parse(inputStream)
        self.originatingEntityID.parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.requestID = inputStream.read_null();
        self.transferEntityID.parse(inputStream)
        self.recordSets.parse(inputStream)



class RecordQueryRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.14 Used to communicate a request for data in record format."""

    def __init__(self):
        """ Initializer for RecordQueryRPdu"""
        super(RecordQueryRPdu, self).__init__()
        self.requestID = 0
        """ request ID"""
        self.pad1 = 0
        """ padding"""
        self.time = 0
        """ time"""
        self.numberOfRecords = 0
        """ numberOfRecords"""
        self.recordIDs = []
        """ record IDs"""
        self.pduType = DisPduType.RECORD_QUERY_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RecordQueryRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.pad1);
        outputStream.write_null(self.time);
        outputStream.write_null( len(self.recordIDs));
        for anObj in self.recordIDs:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RecordQueryRPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.pad1 = inputStream.read_null();
        self.time = inputStream.read_null();
        self.numberOfRecords = inputStream.read_null();
        for idx in range(0, self.numberOfRecords):
            element = null()
            element.parse(inputStream)
            self.recordIDs.append(element)




class EventReportRPdu( SimulationManagementWithReliabilityFamilyPdu ):
    """5.12.4.12 Contains the same information as found in the Event Report PDU."""

    def __init__(self):
        """ Initializer for EventReportRPdu"""
        super(EventReportRPdu, self).__init__()
        self.pad1 = 0
        """ padding"""
        self.numberOfFixedDatumRecords = 0
        """ Fixed datum record count"""
        self.numberOfVariableDatumRecords = 0
        """ variable datum record count"""
        self.fixedDatumRecords = []
        """ Fixed datum records"""
        self.variableDatumRecords = []
        """ Variable datum records"""
        self.pduType = DisPduType.EVENT_REPORT_RELIABLE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EventReportRPdu, self ).serialize(outputStream)
        outputStream.write_null(self.pad1);
        outputStream.write_null( len(self.fixedDatumRecords));
        outputStream.write_null( len(self.variableDatumRecords));
        for anObj in self.fixedDatumRecords:
            anObj.serialize(outputStream)

        for anObj in self.variableDatumRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EventReportRPdu, self).parse(inputStream)
        self.pad1 = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatumRecords.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatumRecords.append(element)




class CreateEntityPdu( SimulationManagementFamilyPdu ):
    """Section 7.5.2. Create a new entity. See 5.6.5.2."""

    def __init__(self):
        """ Initializer for CreateEntityPdu"""
        super(CreateEntityPdu, self).__init__()
        self.requestID = 0
        """ Identifier for the request.  See 6.2.75"""
        self.pduType = DisPduType.CREATE_ENTITY
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( CreateEntityPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( CreateEntityPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();



class RemoveEntityPdu( SimulationManagementFamilyPdu ):
    """Section 7.5.3 The removal of an entity from an exercise shall be communicated with a Remove Entity PDU. See 5.6.5.3."""

    def __init__(self):
        """ Initializer for RemoveEntityPdu"""
        super(RemoveEntityPdu, self).__init__()
        self.requestID = 0
        """ This field shall identify the specific and unique start/resume request being made by the SM"""
        self.pduType = DisPduType.REMOVE_ENTITY
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( RemoveEntityPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( RemoveEntityPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();



class ActionRequestPdu( SimulationManagementFamilyPdu ):
    """ 7.5.7 A request from an SM to a managed entity to perform a specified action. See 5.6.5.7"""

    def __init__(self):
        """ Initializer for ActionRequestPdu"""
        super(ActionRequestPdu, self).__init__()
        self.requestID = 0
        """ identifies the request being made by the simulation manager"""
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.fixedDatums = []
        """ variable length list of fixed datums"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.ACTION_REQUEST
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ActionRequestPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null( len(self.fixedDatums));
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.fixedDatums:
            anObj.serialize(outputStream)

        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ActionRequestPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatums.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class AcknowledgePdu( SimulationManagementFamilyPdu ):
    """7.5.6 Acknowledges the receipt of a Start/Resume PDU, Stop/Freeze PDU, Create Entity PDU, or Remove Entity PDU. See 5.6.5.6."""

    def __init__(self):
        """ Initializer for AcknowledgePdu"""
        super(AcknowledgePdu, self).__init__()
        self.requestID = 0
        """ Request ID that is unique"""
        self.pduType = DisPduType.ACKNOWLEDGE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( AcknowledgePdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( AcknowledgePdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();



class IsPartOfPdu( EntityManagementFamilyPdu ):
    """5.9.5 Used to request hierarchical linkage of separately hosted simulation entities"""

    def __init__(self):
        """ Initializer for IsPartOfPdu"""
        super(IsPartOfPdu, self).__init__()
        self.orginatingEntityID = EntityID();
        """ ID of entity originating PDU"""
        self.receivingEntityID = EntityID();
        """ ID of entity receiving PDU"""
        self.relationship = Relationship();
        """ relationship of joined parts"""
        self.partLocation = Vector3Float();
        """ location of part; centroid of part in host's coordinate system. x=range, y=bearing, z=0"""
        self.namedLocationID = NamedLocationIdentification();
        """ named location"""
        self.partEntityType = EntityType();
        """ entity type"""
        self.pduType = DisPduType.ISPARTOF
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( IsPartOfPdu, self ).serialize(outputStream)
        self.orginatingEntityID.serialize(outputStream)
        self.receivingEntityID.serialize(outputStream)
        self.relationship.serialize(outputStream)
        self.partLocation.serialize(outputStream)
        self.namedLocationID.serialize(outputStream)
        self.partEntityType.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IsPartOfPdu, self).parse(inputStream)
        self.orginatingEntityID.parse(inputStream)
        self.receivingEntityID.parse(inputStream)
        self.relationship.parse(inputStream)
        self.partLocation.parse(inputStream)
        self.namedLocationID.parse(inputStream)
        self.partEntityType.parse(inputStream)



class StopFreezePdu( SimulationManagementFamilyPdu ):
    """Section 7.5.5. Stop or freeze an enity or exercise. See 5.6.5.5"""

    def __init__(self):
        """ Initializer for StopFreezePdu"""
        super(StopFreezePdu, self).__init__()
        self.realWorldTime = ClockTime();
        """ real-world(UTC) time at which the entity shall stop or freeze in the exercise"""
        self.padding1 = 0
        """ padding"""
        self.requestID = 0
        """ Request ID that is unique"""
        self.pduType = DisPduType.STOP_FREEZE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( StopFreezePdu, self ).serialize(outputStream)
        self.realWorldTime.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( StopFreezePdu, self).parse(inputStream)
        self.realWorldTime.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.requestID = inputStream.read_null();



class StartResumePdu( SimulationManagementFamilyPdu ):
    """Section 7.5.4. Start or resume an entity or exercise. See 5.6.5.4."""

    def __init__(self):
        """ Initializer for StartResumePdu"""
        super(StartResumePdu, self).__init__()
        self.realWorldTime = ClockTime();
        """ This field shall specify the real-world time (UTC) at which the entity is to start/resume in the exercise. This information shall be used by the participating simulation applications to start/resume an exercise synchronously. This field shall be represented by a Clock Time record (see 6.2.16)."""
        self.simulationTime = ClockTime();
        """ The reference time within a simulation exercise. This time is established ahead of time by simulation management and is common to all participants in a particular exercise. Simulation time may be either Absolute Time or Relative Time. This field shall be represented by a Clock Time record (see 6.2.16)"""
        self.requestID = 0
        """ Identifier for the specific and unique start/resume request"""
        self.pduType = DisPduType.START_RESUME
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( StartResumePdu, self ).serialize(outputStream)
        self.realWorldTime.serialize(outputStream)
        self.simulationTime.serialize(outputStream)
        outputStream.write_null(self.requestID);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( StartResumePdu, self).parse(inputStream)
        self.realWorldTime.parse(inputStream)
        self.simulationTime.parse(inputStream)
        self.requestID = inputStream.read_null();



class AttributePdu( EntityInformationInteractionFamilyPdu ):
    """7.2.6. Information about individual attributes for a particular entity, other object, or event may be communicated using an Attribute PDU. The Attribute PDU shall not be used to exchange data available in any other PDU except where explicitly mentioned in the PDU issuance instructions within this standard. See 5.3.6."""

    def __init__(self):
        """ Initializer for AttributePdu"""
        super(AttributePdu, self).__init__()
        self.originatingSimulationAddress = SimulationAddress();
        """ This field shall identify the simulation issuing the Attribute PDU. It shall be represented by a Simulation Address record (see 6.2.79)."""
        self.padding1 = 0
        """ Padding"""
        self.padding2 = 0
        """ Padding"""
        self.padding3 = 0
        """ Padding"""
        self.numberAttributeRecordSet = 0
        """ This field shall specify the number of Attribute Record Sets that make up the remainder of the PDU. It shall be represented by a 16-bit unsigned integer."""
        self.attributeRecordSets = []
        self.pduType = DisPduType.ATTRIBUTE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( AttributePdu, self ).serialize(outputStream)
        self.originatingSimulationAddress.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null(self.padding2);
        outputStream.write_null(self.padding3);
        outputStream.write_null( len(self.attributeRecordSets));
        for anObj in self.attributeRecordSets:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( AttributePdu, self).parse(inputStream)
        self.originatingSimulationAddress.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.padding2 = inputStream.read_null();
        self.padding3 = inputStream.read_null();
        self.numberAttributeRecordSet = inputStream.read_null();
        for idx in range(0, self.numberAttributeRecordSet):
            element = null()
            element.parse(inputStream)
            self.attributeRecordSets.append(element)




class InformationOperationsActionPdu( InformationOperationsFamilyPdu ):
    """5.13.3.1 Used to communicate an IO attack or the effects of an IO attack on one or more target entities."""

    def __init__(self):
        """ Initializer for InformationOperationsActionPdu"""
        super(InformationOperationsActionPdu, self).__init__()
        self.receivingSimID = EntityID();
        """ the simulation to which this PDU is addressed"""
        self.requestID = 0
        """ request ID"""
        self.padding1 = 0
        self.ioAttackerID = EntityID();
        self.ioPrimaryTargetID = EntityID();
        self.padding2 = 0
        self.numberOfIORecords = 0
        self.ioRecords = []
        self.pduType = DisPduType.INFORMATION_OPERATIONS_ACTION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( InformationOperationsActionPdu, self ).serialize(outputStream)
        self.receivingSimID.serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.padding1);
        self.ioAttackerID.serialize(outputStream)
        self.ioPrimaryTargetID.serialize(outputStream)
        outputStream.write_null(self.padding2);
        outputStream.write_null( len(self.ioRecords));
        for anObj in self.ioRecords:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( InformationOperationsActionPdu, self).parse(inputStream)
        self.receivingSimID.parse(inputStream)
        self.requestID = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.ioAttackerID.parse(inputStream)
        self.ioPrimaryTargetID.parse(inputStream)
        self.padding2 = inputStream.read_null();
        self.numberOfIORecords = inputStream.read_null();
        for idx in range(0, self.numberOfIORecords):
            element = null()
            element.parse(inputStream)
            self.ioRecords.append(element)




class EventReportPdu( SimulationManagementFamilyPdu ):
    """7.5.12 Reports occurance of a significant event to the simulation manager. See 5.6.5.12."""

    def __init__(self):
        """ Initializer for EventReportPdu"""
        super(EventReportPdu, self).__init__()
        self.padding1 = 0
        """ padding"""
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.fixedDatums = []
        """ variable length list of fixed datums"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.EVENT_REPORT
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EventReportPdu, self ).serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null( len(self.fixedDatums));
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.fixedDatums:
            anObj.serialize(outputStream)

        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EventReportPdu, self).parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatums.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class DataPdu( SimulationManagementFamilyPdu ):
    """7.5.11 Information issued in response to a Data Query PDU or Set Data PDU. Section 5.6.5.11"""

    def __init__(self):
        """ Initializer for DataPdu"""
        super(DataPdu, self).__init__()
        self.requestID = 0
        """ ID of request"""
        self.padding1 = 0
        """ padding"""
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.fixedDatums = []
        """ variable length list of fixed datums"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.DATA
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( DataPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.padding1);
        outputStream.write_null( len(self.fixedDatums));
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.fixedDatums:
            anObj.serialize(outputStream)

        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( DataPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatums.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class EntityStateUpdatePdu( EntityInformationInteractionFamilyPdu ):
    """7.2.5. Nonstatic information about a particular entity may be communicated by issuing an Entity State Update PDU. 5.3.5."""

    def __init__(self):
        """ Initializer for EntityStateUpdatePdu"""
        super(EntityStateUpdatePdu, self).__init__()
        self.entityID = EntityID();
        """ This field shall identify the entity issuing the PDU, and shall be represented by an Entity Identifier record (see 6.2.28)."""
        self.padding1 = 0
        """ Padding"""
        self.numberOfVariableParameters = 0
        """ This field shall specify the number of variable parameters present. This field shall be represented by an 8-bit unsigned integer (see Annex I)."""
        self.entityLinearVelocity = Vector3Float();
        """ This field shall specify an entity's linear velocity. The coordinate system for an entity's linear velocity depends on the dead reckoning algorithm used. This field shall be represented by a Linear Velocity Vector record [see 6.2.95 item c)])."""
        self.entityLocation = Vector3Double();
        """ This field shall specify an entity's physical location in the simulated world and shall be represented by a World Coordinates record (see 6.2.97)."""
        self.entityOrientation = EulerAngles();
        """ This field shall specify an entity's orientation with units of radians and shall be represented by an Euler Angles record (see 6.2.33)."""
        self.entityAppearance = 0
        """ This field shall specify the dynamic changes to the entity's appearance attributes. This field shall be represented by an Entity Appearance record (see 6.2.26)."""
        self.variableParameters = []
        """ This field shall specify the parameter values for each Variable Parameter record that is included (see 6.2.93 and Annex I)."""
        self.pduType = DisPduType.ENTITY_STATE_UPDATE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( EntityStateUpdatePdu, self ).serialize(outputStream)
        self.entityID.serialize(outputStream)
        outputStream.write_null(self.padding1);
        outputStream.write_null( len(self.variableParameters));
        self.entityLinearVelocity.serialize(outputStream)
        self.entityLocation.serialize(outputStream)
        self.entityOrientation.serialize(outputStream)
        outputStream.write_null(self.entityAppearance);
        for anObj in self.variableParameters:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( EntityStateUpdatePdu, self).parse(inputStream)
        self.entityID.parse(inputStream)
        self.padding1 = inputStream.read_null();
        self.numberOfVariableParameters = inputStream.read_null();
        self.entityLinearVelocity.parse(inputStream)
        self.entityLocation.parse(inputStream)
        self.entityOrientation.parse(inputStream)
        self.entityAppearance = inputStream.read_null();
        for idx in range(0, self.numberOfVariableParameters):
            element = null()
            element.parse(inputStream)
            self.variableParameters.append(element)




class CommentPdu( SimulationManagementFamilyPdu ):
    """7.5.13 Used to enter arbitrary messages (character strings, for example). See 5.6.5.13"""

    def __init__(self):
        """ Initializer for CommentPdu"""
        super(CommentPdu, self).__init__()
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records, not used in this Pdu"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.COMMENT
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( CommentPdu, self ).serialize(outputStream)
        outputStream.write_null(self.numberOfFixedDatumRecords);
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( CommentPdu, self).parse(inputStream)
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class SetDataPdu( SimulationManagementFamilyPdu ):
    """Section 7.5.10. Initializing or changing internal state information shall be communicated using a Set Data PDU. See 5.6.5.10"""

    def __init__(self):
        """ Initializer for SetDataPdu"""
        super(SetDataPdu, self).__init__()
        self.requestID = 0
        """ ID of request"""
        self.padding1 = 0
        """ padding"""
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.fixedDatums = []
        """ variable length list of fixed datums"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.SET_DATA
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( SetDataPdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null(self.padding1);
        outputStream.write_null( len(self.fixedDatums));
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.fixedDatums:
            anObj.serialize(outputStream)

        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( SetDataPdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.padding1 = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatums.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class CollisionPdu( EntityInformationInteractionFamilyPdu ):
    """7.2.3 Collisions between entities shall be communicated by issuing a Collision PDU. See 5.3.3."""

    def __init__(self):
        """ Initializer for CollisionPdu"""
        super(CollisionPdu, self).__init__()
        self.issuingEntityID = EntityID();
        """ This field shall identify the entity that is issuing the PDU, and shall be represented by an Entity Identifier record (see 6.2.28)."""
        self.collidingEntityID = EntityID();
        """ This field shall identify the entity that has collided with the issuing entity (see 5.3.3.4). This field shall be represented by an Entity Identifier record (see 6.2.28)."""
        self.eventID = EventIdentifier();
        """ This field shall contain an identification generated by the issuing simulation application to associate related collision events. This field shall be represented by an Event Identifier record (see 6.2.34)."""
        self.pad = 0
        """ some padding"""
        self.velocity = Vector3Float();
        """ This field shall contain the velocity (at the time the collision is detected) of the issuing entity. The velocity shall be represented in world coordinates. This field shall be represented by the Linear Velocity Vector record [see 6.2.95 item c)]."""
        self.mass = 0
        """ This field shall contain the mass of the issuing entity, and shall be represented by a 32-bit floating point number representing kilograms."""
        self.location = Vector3Float();
        """ This field shall specify the location of the collision with respect to the entity with which the issuing entity collided. The Location field shall be represented by an Entity Coordinate Vector record [see 6.2.95 item a)]."""
        self.pduType = DisPduType.COLLISION
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( CollisionPdu, self ).serialize(outputStream)
        self.issuingEntityID.serialize(outputStream)
        self.collidingEntityID.serialize(outputStream)
        self.eventID.serialize(outputStream)
        outputStream.write_null(self.pad);
        self.velocity.serialize(outputStream)
        outputStream.write_null(self.mass);
        self.location.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( CollisionPdu, self).parse(inputStream)
        self.issuingEntityID.parse(inputStream)
        self.collidingEntityID.parse(inputStream)
        self.eventID.parse(inputStream)
        self.pad = inputStream.read_null();
        self.velocity.parse(inputStream)
        self.mass = inputStream.read_null();
        self.location.parse(inputStream)



class ActionResponsePdu( SimulationManagementFamilyPdu ):
    """Section 7.5.8. When an entity receives an Action Request PDU, that entity shall acknowledge the receipt of the Action Request PDU with an Action Response PDU. See 5.6.5.8."""

    def __init__(self):
        """ Initializer for ActionResponsePdu"""
        super(ActionResponsePdu, self).__init__()
        self.requestID = 0
        """ Request ID that is unique"""
        self.numberOfFixedDatumRecords = 0
        """ Number of fixed datum records"""
        self.numberOfVariableDatumRecords = 0
        """ Number of variable datum records, handled automatically by marshaller at run time (and not modifiable by end-user programmers)"""
        self.fixedDatums = []
        """ fixed length list of fixed datums"""
        self.variableDatums = []
        """ variable length list of variable length datums"""
        self.pduType = DisPduType.ACTION_RESPONSE
        """ initialize value """

    def serialize(self, outputStream):
        """serialize the class """
        super( ActionResponsePdu, self ).serialize(outputStream)
        outputStream.write_null(self.requestID);
        outputStream.write_null( len(self.fixedDatums));
        outputStream.write_null( len(self.variableDatums));
        for anObj in self.fixedDatums:
            anObj.serialize(outputStream)

        for anObj in self.variableDatums:
            anObj.serialize(outputStream)



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( ActionResponsePdu, self).parse(inputStream)
        self.requestID = inputStream.read_null();
        self.numberOfFixedDatumRecords = inputStream.read_null();
        self.numberOfVariableDatumRecords = inputStream.read_null();
        for idx in range(0, self.numberOfFixedDatumRecords):
            element = null()
            element.parse(inputStream)
            self.fixedDatums.append(element)

        for idx in range(0, self.numberOfVariableDatumRecords):
            element = null()
            element.parse(inputStream)
            self.variableDatums.append(element)




class GridDataType0( GridData ):
    """6.2.41, table 68"""

    def __init__(self):
        """ Initializer for GridDataType0"""
        super(GridDataType0, self).__init__()
        self.numberOfBytes = 0
        self.dataValues =  []

    def serialize(self, outputStream):
        """serialize the class """
        super( GridDataType0, self ).serialize(outputStream)
        outputStream.write_null(self.numberOfBytes);
        for idx in range(0, 0):
            outputStream.write_null( self.dataValues[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( GridDataType0, self).parse(inputStream)
        self.numberOfBytes = inputStream.read_null();
        self.dataValues = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.dataValues[  idx  ] = val




class GridDataType1( GridData ):
    """6.2.41, table 69"""

    def __init__(self):
        """ Initializer for GridDataType1"""
        super(GridDataType1, self).__init__()
        self.fieldScale = 0
        self.fieldOffset = 0
        self.numberOfValues = 0
        self.dataValues =  []

    def serialize(self, outputStream):
        """serialize the class """
        super( GridDataType1, self ).serialize(outputStream)
        outputStream.write_null(self.fieldScale);
        outputStream.write_null(self.fieldOffset);
        outputStream.write_null(self.numberOfValues);
        for idx in range(0, 0):
            outputStream.write_null( self.dataValues[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( GridDataType1, self).parse(inputStream)
        self.fieldScale = inputStream.read_null();
        self.fieldOffset = inputStream.read_null();
        self.numberOfValues = inputStream.read_null();
        self.dataValues = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.dataValues[  idx  ] = val




class GridDataType2( GridData ):
    """6.2.41, table 70"""

    def __init__(self):
        """ Initializer for GridDataType2"""
        super(GridDataType2, self).__init__()
        self.numberOfValues = 0
        self.padding = 0
        self.dataValues =  []

    def serialize(self, outputStream):
        """serialize the class """
        super( GridDataType2, self ).serialize(outputStream)
        outputStream.write_null(self.numberOfValues);
        outputStream.write_null(self.padding);
        for idx in range(0, 0):
            outputStream.write_null( self.dataValues[ idx ] );



    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( GridDataType2, self).parse(inputStream)
        self.numberOfValues = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.dataValues = [0]*0
        for idx in range(0, 0):
            val = inputStream.read_null
            self.dataValues[  idx  ] = val




class IOCommsNodeRecord( IORecord ):
    """6.2.48.2"""

    def __init__(self):
        """ Initializer for IOCommsNodeRecord"""
        super(IOCommsNodeRecord, self).__init__()
        self.recordLength = 0
        self.padding = 0
        self.commsNodeId = CommunicationsNodeID();

    def serialize(self, outputStream):
        """serialize the class """
        super( IOCommsNodeRecord, self ).serialize(outputStream)
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.padding);
        self.commsNodeId.serialize(outputStream)


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IOCommsNodeRecord, self).parse(inputStream)
        self.recordLength = inputStream.read_null();
        self.padding = inputStream.read_null();
        self.commsNodeId.parse(inputStream)



class IOEffectRecord( IORecord ):
    """6.2.48.3"""

    def __init__(self):
        """ Initializer for IOEffectRecord"""
        super(IOEffectRecord, self).__init__()
        self.recordLength = 0
        self.ioEffectDutyCycle = 0
        self.ioEffectDuration = 0
        self.padding = 0

    def serialize(self, outputStream):
        """serialize the class """
        super( IOEffectRecord, self ).serialize(outputStream)
        outputStream.write_null(self.recordLength);
        outputStream.write_null(self.ioEffectDutyCycle);
        outputStream.write_null(self.ioEffectDuration);
        outputStream.write_null(self.padding);


    def parse(self, inputStream):
        """"Parse a message. This may recursively call embedded objects."""

        super( IOEffectRecord, self).parse(inputStream)
        self.recordLength = inputStream.read_null();
        self.ioEffectDutyCycle = inputStream.read_null();
        self.ioEffectDuration = inputStream.read_null();
        self.padding = inputStream.read_null();


