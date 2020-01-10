package com.kevin.collections.map;

/**
 * @author caonanqing
 * @version 1.0
 * @description     ConcurrentHashMap源码分析
 * @createDate 2019/12/12
 */
public class ConcurrentHashMapSource {

    /**
     *
     * ConcurrentHashMap是HashMap的线程安全版本，内部也是使用（数组 + 链表 + 红黑树）的结构来存储元素。
     * 相比于同样线程安全的HashTable来说，效率等各方面都有极大地提高。
     *
     * 1、构造方法与HashMap对比：没有了HashMap中的threshold和loadFactor，而是改用了sizeCtl来控制，而且只存储了容量在里面。
     *      sizeCtl：
     *          -1：有线程在进行初始化操作。
     *          -(1+nThreads)：有n个线程正在一起扩容。
     *          0：默认值，在真正初始化时使用默认容量。
     *          >0 ：初始化或扩容完成后下一次的扩容门槛。
     *
     * put()       使用的锁主要有（自旋锁+CAS+synchronize+分段锁）
     *      如果桶数组还没初始化或个数为0，则进行初始化，如果待插入的元素所在的桶为空，则尝试将该元素插入桶的第一个位置。
     *      如果正在扩容，则当前线程一起加入到扩容的过程中。
     *      如果待插入的元素所在桶不为空，且不在迁移元素，则锁住这个桶（分段锁）。
     *      如果桶中的元素使用的是链表方式存储，则插入到链表中。
     *      如果桶中的元素是树节点，则调用红黑树的插入方法插入元素。
     *      如果链表元素个数达到了8，则尝试树化。
     *      如果要插入的元素已经是存在，则返回旧值。
     *      如果元素不存在，则Map的元素个数加1，并检查是否需要扩容。
     *
     * remove()
     *      计算hash，使用自旋锁，如果元素所在的桶不存在，则返回null。
     *      如果正在扩容中，则先协助扩容，再继续进行操作。
     *      如果是链表，则遍历链表查找元素，再进行删除。
     *      如果是树，则遍历树查找元素，再进行删除，删除后树较小，则退化成链表。
     *      如果已经删除了元素，则map元素个数减1，并返回旧值。
     *      如果没有找到元素，则返回null。
     *
     * get()
     *      计算hash，找到元素所在的桶。
     *      如果第一个元素就是要找的元素，则返回。
     *      如果是树或者正在扩容，调用各自Node子类的find()查找元素。
     *      如果是链表则遍历整个链表查找元素。
     *
     * size()
     *      元素的个数根据不同的线程放在不用的分段中。
     *      计算counterCell所有的段以及baseCount的数量之和。
     *
     * initTable()：初始化桶
     *      使用CAS控制只能有一个线程初始化。
     *      扩容门槛写死是同桶数组大小的0.75倍，桶数组大小即map的容量，也就是最多存储多少个元素。
     *      sizeCtl在初始化之后存储的是扩容门槛
     *
     * addCount()：判断是否需要扩容
     *      思想与LongAdder类是一样的，把数组的大小存储根据不同的线程存储到不同的段上（分段锁思想），减少不同线程同时更新size时的冲突。
     *      计算元素个数时把这些段的值及baseCount相加算出总的元素个数。
     *      如果元素个数达到了扩容门槛，则进行扩容，扩容门槛为容量的0.75倍。
     *      扩容时sizeCtl高位存储扩容邮戳(resizeStamp)，低位存储扩容线程数加1（1+nThreads）。
     *      当有其它线程添加元素时发现存在扩容，则加入扩容行列。
     *
     * helpTransfer()：协助扩容（迁移元素）
     *      添加元素时发现正在扩容，且当前元素所在的桶元素已经迁移完成了，则协助迁移其它桶的元素。
     *
     * transfer()：迁移元素
     *      新桶数组大小是旧桶数组的两倍。
     *      迁移元素先从靠后的桶开始。
     *      迁移完成的桶在里面放置一个ForwardingNode类型的元素，标记该桶迁移完成。
     *      迁移时根据hash&n是否等于0把桶中元素分化成两个链表或树。
     *      低位链表（树）存储在原来的位置。
     *      高们链表（树）存储在原来的位置加n的位置。
     *      迁移元素时会锁住当前桶，也是分段锁的思想。
     *
     * 设计优秀之处：
     *      CAS+自旋，乐观锁的思想，减少线程上下文切换的时间。
     *      分段锁的思想，减少同一把锁争抢时带来的低效问题。
     *      CounterCell，分段存储元素个数，减少多线程同时更新一个字段带来的低效。
     *      多线程协助扩容
     *
     * 总结
     *
     *
     * 存储结构：数组+链表+红黑树
     *
     */

}
