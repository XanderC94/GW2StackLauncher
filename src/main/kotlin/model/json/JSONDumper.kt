package model.json

import model.Dumper

object JSONDumper : Dumper(mapper = JSONMapper)