# Load-Balancing-in-Servers

YOUTUBE LINK : https://youtu.be/L4z7mTV_0Ew

Suppose multiple clients simultaneously make a request for a file(same or different) from a server.

Now if the number of clients are below the threshold value, then one server can easily fulfill all their requests based on some priority(burst time , throughput etc.)

But if clients count increases we have to make copies of the server, 
but problem is how to decide which server to send the request and how much requests count  it can handle at a time.


Techniques to be used :


Multithreading (multiple clients can make request at the same time).

CPU Scheduling (There will be many requests/processes so we have prioritize them using scheduling algorithm)

Server Scheduling (There will be multiple servers in our program, we have to make sure if load on a server increases, different server should be handling those requests)

MySql Database( to store the content in a organized and easily retrievable manner)






