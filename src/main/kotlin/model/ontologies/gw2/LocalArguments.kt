package model.ontologies.gw2

data class LocalArguments(var arguments:List<String>) {

    companion object : () -> LocalArguments {
        override fun invoke(): LocalArguments {
            return LocalArguments(listOf())
        }
    }
}
