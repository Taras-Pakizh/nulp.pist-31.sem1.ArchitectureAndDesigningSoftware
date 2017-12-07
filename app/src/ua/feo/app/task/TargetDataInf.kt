package ua.feo.app.task

interface TargetDataInf : TargetInf {

    override fun getName(): String

    override fun getTestCaseList(): List<String>

    override fun getEtalonTestCaseList(): Map<String, Boolean>

    fun getTarget(): TargetInf

}