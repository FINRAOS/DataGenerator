Social Network Friendship org.finra.datagenerator.common.Graph Example

There are two examples doing the exact same thing -- one in Scala and one in Java.

The class User defines a social network user.
UserType defines the different types that user can be (Admin, SocialNetworkEmployee, or PublicUser).
A UserStub is a template for creating a user, with the type defined, and possibly other fields, but with not everything
needed to create a user being defined yet. In the example we're not really making use of this, but parameterizing our
Graphs around UserStub instead of UserType is more extensible -- it might allow us, in another implementation,
to ensure the users in our generated graphs have specific attributes.

Users can be linked to other users in a graph, denoting friendships. The graph is directed, with the parent of another
user being the user that requested the friendship.
The graph is also acyclic, which obviously isn't ideal for a social network friendship graph, but with some work it 
should be possible to remove this restriction (e.g., there are places in other engines implementing this code that
assume a graph always has at least one root node, so if we allowed cyclic graphs, making the graph acyclic would still
need to be an option).

Each UserType additionally defines the possible parents and children that user type can have.
In a more realistic implementation, we would also place restrictions on things like geographical location or number of
hops between nearest friend, or secret accounts, before allowing a friendship request, but that would complicate
matters considerably, and this is just a simple example.

A few methods are either left unimplemented (as in UserTransitions)
or, though implemented, are not being used (as in UserTypes).
If we wanted to translate our "User Stub Graphs" into "User Graphs" -- that is, to fill in the names, birth dates,
geographical information, etc. -- then we would need to finish implementing these methods, which are used for expanding
a graph and populating it with random data.

Over time, the hope is that additional generation engines/methods, including random graph expansion,
translation from stubs to data, and generation using DOT input files, will be added to this common library.
