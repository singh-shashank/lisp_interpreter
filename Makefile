# This is a very simple makefile for building the Lisp interpreter
# project when using Java on stdsun. Feel free to add any improvements:
# e.g. pattern rules, automatic tracking of dependencies, etc. There
# is a lot of info about "make" on the web.

# Java compiler
JAVAC = javac

# Java compiler flags
JAVAFLAGS = -g -classpath bin

# Creating a .class file
COMPILE = $(JAVAC) $(JAVAFLAGS)

SOURCE_FILES = src/LispIntException.java src/OutputHandler.java src/Token.java \
				src/Lex.java src/SExp.java src/EvalSExp.java src/Parser.java \
				src/MyInt.java#$(wildcard src/*.java)


# One of these should be the "main" class listed in Runfile
CLASS_FILES = $(shell echo $(SOURCE_FILES) | sed s/src/bin/g | sed s/.java/.class/g)

# The first target is the one that is executed when you invoke
# "make". 

all: $(CLASS_FILES) 

# The line describing the action starts with <TAB>
bin/%.class : src/%.java
	$(COMPILE) $^ -d bin

clean:
	rm -rf bin/*.class


