import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.readLines
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalPathApi::class)
fun fileReader(file: String): List<Int> {
    return Paths.get(file).readLines().map { it.toInt() }
}

// https://en.wikipedia.org/wiki/Bucket_sort
fun group(it: Int, max: Int, bucketCount: Int) = it.div(max.toDouble()).times(bucketCount - 1.0).roundToInt()

fun immutableBucketSort(array: List<Int>): List<Int> {
    val max = array.maxOrNull() ?: return array
    val bucketCount = sqrt(array.size.toDouble()).roundToInt()

    return array
        .groupBy { group(it, max, bucketCount) }
        .toSortedMap()
        .map { it.value.sorted() }
        .flatten()
}

// https://en.wikipedia.org/wiki/Bucket_sort
fun bucketSort(array: List<Int>): List<Int> {
    if (array.isEmpty()) {
        return array
    }

    val max = array.maxOrNull()!!
    val bucketCount = sqrt(array.size.toDouble()).roundToInt()

    val buckets = (0 until bucketCount).map { mutableListOf<Int>() }.toMutableList()

    for (element in array) {
        buckets[group(element, max, bucketCount)].add(element)
    }

    for (bucket in buckets) {
        bucket.sort()
    }

    return buckets.flatten()
}

fun bucketDebug(array: List<Int>): List<List<Int>> {
    val max = array.maxOrNull() ?: return emptyList()
    val bucketCount = sqrt(array.size.toDouble()).roundToInt()

    return array
        .groupBy { group(it, max, bucketCount) }
        .toSortedMap()
        .map { it.value }
}

// https://www.geeksforgeeks.org/c-program-for-binary-search-recursive-and-iterative/
fun binSearch(array: List<Int>, value: Int, start: Int = 0, end: Int = array.size, stack: List<Int> = emptyList()): Pair<Int?, List<Int>> {
    if (end < start) {
        return null to stack
    }

    val middleIndex = start + (end - start) / 2
    val middleValue = array[middleIndex]

    return when {
        middleValue > value -> binSearch(array, value, start, middleIndex-1, stack + middleValue)
        middleValue < value -> binSearch(array, value, middleIndex+1, end, stack + middleValue)
        else -> middleIndex to stack + middleIndex
    }
}

fun seqSearch(array: List<Int>, value: Int) = array.indexOfFirst(value::equals)

@ExperimentalTime
fun main() {
    val input = run {
        print("File Path: ")
        fileReader(readLine().orEmpty())
    }

    println("\nBuckets:")
    bucketDebug(input).forEachIndexed { index, list ->
        println("> Bucket $index: $list")
    }

    val (output, timeImperative) = measureTimedValue { bucketSort(input) }
    val timeKt = measureTime { immutableBucketSort(input) }

    println("\nSort Result:")
    println("> Sorted Array                 : $output")
    println("> Mutable w/ Imperative Syntax : ${timeImperative.inMilliseconds}ms")
    println("> Immutable w/ Kotlin Syntax   : ${timeKt.inMilliseconds}ms")

    val find = run {
        print("\nType a number to search: ")
        readLine().orEmpty().toInt()
    }

    val (binSearch, binSearchTime) = measureTimedValue { binSearch(output, find) }
    val seqSearchTime = measureTime { seqSearch(output, find) }

    println("\nBinSearch Result:")
    println("> Index: ${binSearch.first ?: "Not Found"}")
    println("> Stack: ${binSearch.second}")
    println("> Bin Search Time: ${binSearchTime.inMilliseconds}ms")
    println("> Seq Search Time: ${seqSearchTime.inMilliseconds}ms")
    // Thread.sleep(60 * 1000 * 60)
}
