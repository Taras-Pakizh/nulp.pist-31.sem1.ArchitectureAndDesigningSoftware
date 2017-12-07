package ua.feo.app.task

import java.util.ArrayList
import java.util.HashMap

class TargetData1 : TargetDataInf {

    private var data: HashMap<String, Boolean> = HashMap()

    init {
        with(data){
            put("ArithmeticException", false)
            put("Помилка вводу в поле A", false)
            put("Коректність написів", true)
            put("Ігнорування даних поля A", true)
            put("Помилка при введенні від'ємних чисел", false)
            put("Опрацювання помилок", true)
            put("Помилка при введенні від'ємних чисел", false)
        }
    }

    override fun getName(): String = "Трьохвимірна гратка"

    override fun getTestCaseList(): List<String> = ArrayList(data.keys)

    override fun getEtalonTestCaseList(): Map<String, Boolean> = data

    override fun getTarget(): TargetInf = Target1()

}