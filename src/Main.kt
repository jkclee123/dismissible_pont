package dismissible

import kotlin.random.Random
import kotlin.system.measureTimeMillis

const val dimension: Int = 3
const val pointCount: Int = 1000000

data class Point(val id: Int, val dimList: List<Float>)

fun main(args: Array<String>){
    var elapsedTime: Long
    val ptList: List<Point> = genPoints()
    var resultPtList: List<Point> = emptyList()
    elapsedTime = measureTimeMillis { resultPtList = bruteForceMethod(ptList) }
    println("secure points = ${resultPtList.size}, elapsedTime = ${elapsedTime}ms")
    elapsedTime = measureTimeMillis { resultPtList = filterMethod(ptList) }
    println("secure points = ${resultPtList.size}, elapsedTime = ${elapsedTime}ms")
    println()

}

fun filterMethod(ptList: List<Point>): List<Point>{
//    primePt: point with the smallest value of largest dimension of each point
    val primePt: Point = ptList.minBy { it.dimList.sum() }!!
//    use primePt to kill other point
    val filterPtList: List<Point> = oneBruteForce(primePt, ptList)

    var lowerPtList: MutableList<Point> = emptyList<Point>().toMutableList()
    var upperPtList: MutableList<Point> = emptyList<Point>().toMutableList()
    val combinePtList: MutableList<Point> = emptyList<Point>().toMutableList()
//    split filterPtList to lower and upper
//    lowerPtList: points with all dimension le 0.5
    for (pt in filterPtList) {
        var lower = true
        for (dim in 0..(dimension - 1)) {
            if (pt.dimList[dim] >= 0.5) {
                lower = false
                break
            }
        }
        if (lower) {
            lowerPtList.add(pt)
        } else {
            upperPtList.add(pt)
        }
    }
//    trim lowerPtList
    lowerPtList = bruteForce(lowerPtList).toMutableList()
//    use lowerPtList to kill upperPtList
    upperPtList = masterBruteForce(lowerPtList, upperPtList).toMutableList()
    combinePtList.addAll(lowerPtList)
    combinePtList.addAll(upperPtList)
//    final self brute force
    return bruteForce(combinePtList)
}

fun bruteForceMethod(ptList: List<Point>): List<Point>{
    return bruteForce(ptList)
}

fun bruteForce(ptList: List<Point>): List<Point>{
    return ptList.filter { holdPt ->
        var secure = false
        for (compPt in ptList){
            secure = false
            for (dim in 0..(dimension - 1)){
                if (compPt.dimList[dim] >= holdPt.dimList[dim]){
                    secure = true
                    break
                }
            }
            if (!secure) break

        }
        secure
    }
}

fun masterBruteForce(masterPtList: List<Point>, slavePtList: List<Point>): List<Point>{
    return slavePtList.filter { slavePt ->
        var secure = false
        for (masterPt in masterPtList){
            secure = false
            for (dim in 0..(dimension - 1)){
                if (masterPt.dimList[dim] >= slavePt.dimList[dim]){
                    secure = true
                    break
                }
            }
            if (!secure) break
        }
        secure
    }
}

fun oneBruteForce(primePt: Point, ptList: List<Point>): List<Point>{
    return ptList.filter { pt ->
        var secure = false
        for (dim in 0..(dimension - 1)){
            if (primePt.dimList[dim] >= pt.dimList[dim]){
                secure = true
                break
            }
        }
        secure
    }
}

fun genPoints(): List<Point>{
    val ptList = mutableListOf<Point>()
    for (id in 0..(pointCount-1)){
        val fltList = mutableListOf<Float>()
        for (dim in 0..(dimension-1)){
            fltList.add(Random.nextFloat())
        }
        ptList.add(Point(id, fltList))
    }
    return ptList
}