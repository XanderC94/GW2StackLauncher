package model.json

import model.Parser

object JSONParser : Parser(mapper = JSONMapper)