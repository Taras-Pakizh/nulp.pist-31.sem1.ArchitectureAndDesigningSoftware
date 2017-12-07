package ua.feo.app.inf

interface RouterInf {

    fun goTo(res : String) : Window

    fun getController(res : String) : Window

}