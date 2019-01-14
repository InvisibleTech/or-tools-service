# OR Tools REST Server
## Services Provided
Currently, this version of the server supports the following OR Tools Services:

* Maximal Flow

Use of the service is via JSON payloads but the names in the payloads are analogous
to those referenced in the Google OR Tools documentation.

## Purpose
This project is meant to provide a simple REST API for using Google OR Tools.  Since
OR Tools is not delivered officially by any means of package manager or repository,
Docker was used to provide a way to install the C binaries needed.

The other point of the REST service is to provide a mnemonic way to refer to the
nodes on the graph representing problems to be solved. Instead of using array index
values, you use names.  Internally the service associates the names with index values
and calls the OR Tools API using those.  Results are mapped back from index values
to names.

## Sample Request

The request is done via an `HTTP POST` using `Encoding: UTF-8` and
`Content-Type: applicaiton/json`.

    {
        source: "Here",
        sink: "There",
        arcs: [
          {
            tail: "Here",
            head: "This",
            capacity: 130
          },
          {
            tail: "This",
            head: "There",
            capacity: 15
          },
          {
              tail: "Here",
              head: "There",
              capacity: 35
          }
        ]
    }

This will yield:

    {
       "status":"OPTIMAL",
       "optimalFlow":50,
       "arcs":[
          {
            "tail":"Here",
            "head":"This",
            "maxFlow":15,
            "capacity":130
          },
          {
            "tail":"This",
            "head":"There",
            "maxFlow":15,
            "capacity":15
          },
          {
            "tail":"Here",
            "head":"There",
            "maxFlow":35,
            "capacity":35
          }
       ]
    }
