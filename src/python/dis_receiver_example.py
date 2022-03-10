#!python

__author__ = "mcgredo"
__date__ = "$Jun 25, 2015 12:10:26 PM$"

import socket
import time
import sys
import array
import os

from opendis7 import EntityStatePdu
from RangeCoordinates import GPS
from PduFactory import createPdu

UDP_PORT = 1999

udp_recvr_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_recvr_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
udp_recvr_socket.bind(("", UDP_PORT))

print("Listening for DIS on UDP socket {}".format(UDP_PORT))

gps = GPS()

def recv():
    data = udp_recvr_socket.recv(1024) # buffer size in bytes
    pdu = createPdu(data);
    pduTypeName = pdu.__class__.__name__
    if pdu.pduType == 1: #PduTypeDecoders.EntityStatePdu:
        loc = (pdu.entityLocation.x, pdu.entityLocation.y, pdu.entityLocation.z)
        lla = gps.ecef2lla(loc)
        print("Received {}. Id: {}, Location: {} {} {}".format(pduTypeName, pdu.entityID.entityID, lla[0], lla[1], lla[2]))
    else:
        print("Received {}, {} bytes".format(pduTypeName, len(data)), flush=True)
    # we got our SmokeTest packet -> exit w/okay
    os._exit(os.EX_OK)

while True:
    recv()
