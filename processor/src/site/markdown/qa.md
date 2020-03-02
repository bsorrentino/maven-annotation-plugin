## Questions & Answers


### Multiple executions of plugin

Referring to issue [72](https://github.com/bsorrentino/maven-annotation-plugin/issues/72), apparently the default configuration of the sources plugin doesn't play nice with plugins bound to the `generate-sources` phase.

In the case you having this issue please refer to: [How to prevent generate-sources phase executing twice](http://blog.peterlynch.ca/2010/05/maven-how-to-prevent-generate-sources.html)
