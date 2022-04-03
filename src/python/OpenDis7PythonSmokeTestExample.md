
In a terminal, run the receiver:  
```python3 dis_receiver_example.py```  

In another terminal, run the sender:  
```python3 dis_sender_example.py```  

You should also see the traffic on the net in Wireshark on your localhost interface.

Expected output from the reciever:  

    Listening for DIS on UDP socket 1999  
    Received EntityStatePdu. Id: 42, Location: 36.59999999999997 -121.9 0.9999997513368726  

Expected output from the sender:  

    Sent EntityStatePdu. 144 bytes  



