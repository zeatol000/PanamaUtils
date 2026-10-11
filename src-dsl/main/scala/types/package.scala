package zea.panama.dsl

/** This package represents complex native types. 
 * Types defined in [[zea.panama.dsl]] are "simple" in the sense that they can
 * be represented easily in C. E.g.,
 *    arr: Arr[Int, 32]     int arr[32]
 *    ptr: Ptr[Int]         int *ptr
 *    fp:  Int *=> Int      int (*fp)(int)
 *    fn:  Int &=> Int      int fn(int)
 * This package, however, gives tools to represent types that are user-defined.
 * Such as structs, unions, and enums.
 */
package object types {

}
