## Bank Project with OAuth2

This project has been built to learn how the OAuth2 principles works

Basically, the OAuth2 Protocol exists to make the system limited to some data, like passwords or login credentials.

Parts of the protocol:
 - Resource Owner (Who wants to use the system)
 - Client (Who is an intermediate authorization grant and retrieve tokens)
 - Authentication Server (The system responsible for authenticating the user and sending tokens)
 - Resource Server (Where the data is stored, being the domain api)

Flow: <br>

The client directs the user to the auth server, and when the user is authenticated, the
auth server asks the user if him let the client send his information to the Resource Server.

When the grant is gained, the Auth Server redirects the Client with a temporary code, and the Client can send this code to get a Token.

The Token will be used to the Client access the Resource Server which only handles with tokens, and no more authentication ways, being stateless

#### The Project So Far

The project is still in early stage, and it won't work now.

I designed the system to work with events and messages with RabbitMQ, but a problem
appeared: asynchronous validations!

The project was designed to when the resource owner registers in the system that passes through the auth server first,
a message is sent to the resource server (the api) to register it domain (client bank and his account).

Even it looks fancy, the resource server (the api) has to validate the input, but since the message/event was sent expecting everything is ok,
the api will invalidate an input when invalid, and the error will never be printed to the user.

So I decided to create an orchestrator api (api gateway) to orchestrate both apis (the resource server and the auth server)

----- 

I've been using the Postman as a Client since my front end skills are not good enough to consume these apis.



