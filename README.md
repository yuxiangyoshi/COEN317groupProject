# COEN317groupProject

Logical parts:

1. Subscriber - Publisher relationship database handler. (Manipulating and read from a .txt file)
	
2. Publishers' message handler. (define protocol on how to communicate. messages will be sent by sockets)
	
3. Subscribers' message handler
	
4. Master message handler. (receiving messages, distributing works to different servers. may be responsible for data sync)
	
5. Front end: message sender and receiver/display. (optional) Publisher & Subscriber UI