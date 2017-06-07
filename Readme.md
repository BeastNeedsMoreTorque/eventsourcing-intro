# Introduction to Event Sourcing and CQRS in Scala


The purpose of this repository it to provide minimal examples, illustrating the core principles of Event Sourcing and CQRS, in the Scala language. I used the code for an introductory presentation on the topic. [[Slides on SlideShare]](https://www.slideshare.net/lutzh/introduction-to-event-sourcing-and-cqrs-in-scala-scaladays-copenhagen-262017)

The example use case is a football league. The desired outcome is a representation of the league table, indicating how many games each team has played and how many points they have gathered.

Initially, only an implementation with the Lagom framework has been done.


### To do
Clean up, add tests and some commments.

Implement further examples, in Akka Persistence, fun.CQRS and Eventuate.

### Caveats
I coded this in a bit of a rush, shortly before the presentation. So the code is far from perfect and could use some refinement. And I didn't even get to implement the example in the other frameworks I mentioned in the talk yet, which I want to do for comparison. If you're interested in that, please check back in a few weeks.

Suggestions for improvements are welcome, through issues or PRs.

