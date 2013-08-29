# lein-suck-deps

Symlinks or makes copies of all project dependencies to the given target
directory. Can be useful when you want to use the products of Leiningen
projects in an environment where you for some reason can't use Leiningen.

## Status

This is a quick hack that I made for myself. It is likely that this project
will not see much maintenance. However should you want to send a pull request I
will cladly receive them and merge accordingly.

## Installation

This library is unpublished in sense that you will not find the jar available
through the Maven ecosystem. To use this library you need to get the sources,
build and install the jar to your local Maven repository, and tell Leiningen to
look for plug-ins from there.

    $ git clone git@github.com:mjhanninen/lein-suck-deps.git
    $ cd lein-suck-deps
    $ lein install

Then update ~/.lein/profiles.clj to contain the following:

    {:user {:plugins [[lein-suck-deps "0.1.0-SNAPSHOT"]]}}

## Usage

To create links to the dependencies of `myproject` inside the directory
`~/lib/myproject/3.14`:

    $ cd path/to/myproject
    $ lein suck-deps ~/lib/myproject/3.14

In case you actually wanted to copy the dependencies instead of symlinking:

    $ lein suck-deps ~/lib/myproject/3.14 copy

In case you are not sure what you are doing:

    $ lein suck-deps ~/lib/myproject/3.14 dry-run

The script requires that the target directory exists.

## License

Copyright © 2013 Matti Hänninen

Distributed under the Eclipse Public License, the same as Clojure.
