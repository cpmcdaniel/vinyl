# vinyl

Sample application that reads input "records" and provides soreted output.

## Usage

Run the project directly:

    $ clojure -m cpmcdaniel.vinyl samples/file-pipes.txt samples/file-commas.txt samples/file-spaces.txt

Or, to serve up the REST API:

    $ clojure -m cpmcdaniel.vinyl 

Run the project's tests:

    $ clojure -A:test:runner

## License

Copyright © 2020 Craig McDaniel

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
