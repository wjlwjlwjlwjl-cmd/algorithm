# 一、双指针

## 1. 复写零

给你一个长度固定的整数数组 arr ，请你将该数组中出现的每个零都复写一遍，并将其余的元素向右平移。
注意：请不要在超过该数组长度的位置写入元素。请对输入的数组 就地 进行上述修改，不要从函数返回任何东西。

**示例 1：**
输入：arr = `[1,0,2,3,0,4,5,0]`
输出：`[1,0,0,2,3,0,0,4]`
解释：调用函数后，输入的数组将被修改为：`[1,0,0,2,3,0,0,4]`

**示例 2：**
输入：arr = `[1,2,3]`
输出：`[1,2,3]`
解释：调用函数后，输入的数组将被修改为：`[1,2,3]`

---

这道题可以开空间修改，能 AC，而且还能超过 94%......

原地修改，我们的思路是：用 cur 表示原数组我们的判断位置，des 表示修改过后的数组对应到 cur 的位置，所以 des 要从 -1 开始

> 这是最不好理解的一个点。就以第一个元素为例，如果非零，那么就意味着修改前后的数组，cur 和 des 是对应的（先由 cur 判断 des 的情况）；如果为零，那么就要复写，cur 对应到再下一个，所以是 des += 2
> 
> 或者说，不从这样的逻辑上来推。des 表示 **我要把元素放到那个位置**。一开始时，还没有判断 cur 的情况，所以自然不能直接从零开始

然后是第二个问题，我们的 des 可能会超出数组。我们采用如下方式处理：

1. 如果 des == n - 1，那么不必再往后移动，直接终止就可以了，同时我们的 cur 也不需要再移动（cur 每往后移动一次都意味着再判断一轮 des 的位置）
2. 如果 des == n，那么当前 n 的位置就是某一次复写零之后新增的零的位置，但是它已经越界了。而它的前面还应该有一个零，所以我们直接让 `arr[n - 1] = 0` 并且让 des -= 2 （我们这里已经写回了两个零了） 

```java
class Solution {
    public void duplicateZeros(int[] arr) {
        int n = arr.length;
        int cur = 0, des = -1;
        while(cur < n){
            if(arr[cur] == 0){
                des += 2;
            }
            else{
                des += 1;
            }
            if(des >= n - 1){
                break;
            }
            cur++;
        }
        if(des == n){
            arr[n - 1] = 0;
            des -= 2;
            cur--;
        }
        while(cur >= 0){
            if(arr[cur] == 0){
                arr[des--] = 0;
                arr[des--] = 0;
            }
            else{
                arr[des--] = arr[cur];
            }
            cur--;
        }
    }
}
```

# 二、滑动窗口

## 1. 最小覆盖子串

给定两个字符串 s 和 t，长度分别是 m 和 n，返回 s 中的 最短窗口 子串，使得该子串包含 t 中的每一个字符（包括重复字符）。如果没有这样的子串，返回空字符串 ""。

测试用例保证答案唯一。

**示例 1：**

输入：s = "ADOBECODEBANC", t = "ABC"
输出："BANC"
解释：最小覆盖子串 "BANC" 包含来自字符串 t 的 'A'、'B' 和 'C'。

**示例 2：**

输入：s = "a", t = "a"
输出："a"
解释：整个字符串 s 是最小覆盖子串。

**示例 3:**

输入: s = "a", t = "aa"
输出: ""
解释: t 中两个字符 'a' 均应包含在 s 的子串中，因此没有符合条件的子字符串，返回空字符串。

---

滑动窗口的题目，所做的事情可以认为只有两件：

1. 入窗口，调整内容
2. 出窗口，调整内容，观察是否符合条件，更新结果

用模板大体表示这类题目的结构

```java
while(right < border){
    something_with_right_done;
    while(condition_given){
        something_with_left_done;
        left++;
    }
    right++;
}
```

对于这道题而言，我们关心的是 s 中能够凑齐 t 中的字符。这里的凑齐包含两个意思：“个数”和“种类”都凑齐

因此我们使用 `hash` 统计种类+次数。在窗口中，我们不断更新 s 在窗口中的部分所有字母的种类和次数，当某次更新凑够了一个字母，就让 tmp（凑够字母的个数）+ 1。如果 tmp == count（需要凑够的个数），这个时候就得到一个结果了，所以我们开始更新结果，并不断收缩窗口，直到不再凑齐字母

以此往复，最后就可以得到最短的子串

```java
class Solution {
    public String minWindow(String s, String t) {
        int left = 0, right = 0, count = 0, tmp = 0;
        int m = s.length(), n = t.length();
        HashMap<Character, Integer> hash = new HashMap<>();
        for(int i = 0; i < n; i++){
            char ch = t.charAt(i);
            if(!hash.containsKey(ch)){
                count++;
            }
            hash.put(ch, hash.getOrDefault(ch, 0) + 1);
        }
        HashMap<Character, Integer> hash2 = new HashMap<>();
        int len = Integer.MAX_VALUE, begin = -1;
        while(right < m){
            char in = s.charAt(right);
            hash2.put(in, hash2.getOrDefault(in, 0) + 1);
            if(hash.containsKey(in) && hash2.get(in).equals(hash.get(in))){
                tmp++;
            }
            while(tmp == count){
                if(len > right - left + 1){
                    len = right - left + 1;
                    begin = left;
                }
                char out = s.charAt(left);
                if(hash.containsKey(out) && hash2.get(out).equals(hash.get(out))){
                    tmp--;
                }
                hash2.put(out, hash2.get(out) - 1);
                left++;
            }
            right++;
        }
        if(begin == -1){
            return "";
        }

        return s.substring(begin, begin + len);
    }
}
```

# 三、二分查找

给你一个按照非递减顺序排列的整数数组 nums，和一个目标值 target。请你找出给定目标值在数组中的开始位置和结束位置。

如果数组中不存在目标值 target，返回 `[-1, -1]`。

你必须设计并实现时间复杂度为 O(log n) 的算法解决此问题。

**示例 1：**

输入：nums = [5,7,7,8,8,10], target = 8
输出：[3,4]

**示例 2：**

输入：nums = [5,7,7,8,8,10], target = 6
输出：[-1,-1]

**示例 3：**

输入：nums = [], target = 0
输出：[-1,-1]

---

二分查找最麻烦的就是怎样避免陷入死循环，但实际上这类题是有模板的。首先需要明确我们将整个区间分为了三个部分：小于、等于、大于。这里不一定是数值上的大小关系，只要能够满足把区间分成这种三个部分的都可以通过二分来做，或者题目中有要求实现时间复杂度为 `logn` 的，大概率就是通过二分来做了

根据我们需要找到的是中间部分的左边界还是右边界，我们有两种模板可以使用

```java
//找左边界
while(left < right){
    int mid = left + (right - left) / 2;
    if(nums[mid] < target){
        left = mid + 1;
    }
    else{
        right = mid;
    }
}

//找右边界
while(left < right){
    int mid = left + (right - left) / 2;
    if(nums[mid] > target){
        right = mid - 1;
    }
    else{
        left = mid;
    }
}
```

解决二分查找问题关键就是：理解什么是左边界，什么是右边界，二分查找的时候找的不光是中间那个点，而是符合查找条件的区间的端点，剩下的就是根据要求判断要找的是左端点还是右端点

```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int n = nums.length;
        int left = 0, right = n - 1;
        int[] ret = new int[2];
        ret[0] = ret[1] = -1;
        if(n == 0){
            return ret;
        }
        while(left < right){
            int mid = left + (right - left) / 2;
            if(nums[mid] < target){
                left = mid + 1;
            }
            else{
                right = mid;
            }
        }
        if(nums[left] != target){
            return ret;
        }
        ret[0] = left;
        left = 0;
        right = n - 1;
        while(left < right){
            int mid = left + (right - left + 1) / 2;
            if(nums[mid] > target){
                right = mid - 1;
            }
            else{
                left = mid;
            }
        }
        ret[1] = left;
        return ret;
    }
}
```

> 另外还有一道，就是对这种思路的练习，就不写在这里了 https://leetcode.cn/problems/find-peak-element/

# 四、前缀和

所谓“前缀”，其实就是某个位置及这个位置之前的所有部分的总和，通过一个额外的数组或者哈希表（时间优化）来记录整体的情况，从某种意义上来说思想可能和动态规划有相似之处

这里主要有两种题型，一种就是直接根据几何关系，判断当前位置的前缀与前面某个位置的前缀的关系进而得出结果；另一种就是得出某种中间区间，这里的区间，就是由当前位置的前缀抠出前面某个位置的前缀的到

给定一个整数数组 nums 和一个整数 k ，返回其中元素之和可被 k 整除的非空 子数组 的数目。

子数组 是数组中 连续 的部分。

---
## 1. 可被k整除的子数组

**示例 1：**

输入：nums = [4,5,0,-2,-3,1], k = 5
输出：7
解释：
有 7 个子数组满足其元素之和可被 k = 5 整除：`[4, 5, 0, -2, -3, 1], [5], [5, 0], [5, 0, -2, -3], [0], [0, -2, -3], [-2, -3]`

**示例 2:**

输入: nums = [5], k = 9
输出: 0

```java
class Solution {
    public int subarraysDivByK(int[] nums, int k) {
        int n = nums.length;
        HashMap<Integer, Integer> hash = new HashMap<>();
        hash.put(0, 1);
        int sum = 0, ret = 0;
        for(int i = 0; i < n; i++){
            sum += nums[i];
            int tmp = (sum % k + k) % k;
            if(hash.containsKey(tmp)){
                ret += hash.get(tmp);
            }
            hash.put(tmp, hash.getOrDefault(tmp, 0) + 1);
        }
        return ret;
    }
}
```

1. 负数取余。对于有负数的取余操作，结果是正是负取决于第一个数的正负，对于负数的取余操作和正数一样靠向零，比如 `-5 % 3 = -2`。
2. 前缀和。某个中间区间，都可以看作一个大前缀扣掉从头开始的某个小前缀；这里如果一个区间可以被整除的话，那么要么他的小前缀、大前缀都能被整除，要么大小前缀的余数都相同

---

## 2. 数量相同的子数组

给定一个二进制数组 nums , 找到含有相同数量的 0 和 1 的最长连续子数组，并返回该子数组的长度。

**示例 1：**

输入：nums = [0,1]
输出：2
说明：[0, 1] 是具有相同数量 0 和 1 的最长连续子数组。

**示例 2：**

输入：nums = [0,1,0]
输出：2
说明：[0, 1] (或 [1, 0]) 是具有相同数量 0 和 1 的最长连续子数组。

**示例 3：**

输入：nums = [0,1,1,1,1,1,0,0,0]
输出：6
解释：[1,1,1,0,0,0] 是具有相同数量 0 和 1 的最长连续子数组。

```java
class Solution {
    public int findMaxLength(int[] nums) {
        int n = nums.length;
        for(int i = 0; i < n; i++){
            if(nums[i] == 0){
                nums[i] = -1;
            }
        }
        int ret = 0, sum = 0;
        HashMap<Integer, Integer> hash = new HashMap<>();
        hash.put(0, -1);
        for(int i = 0; i < n; i++){
            sum += nums[i];
            if(hash.containsKey(sum)){
                ret = Integer.max(ret, i - hash.get(sum));
            }
            else{
                hash.put(sum, i);
            }
        }
        return ret;
    }
}
```

这里主要的巧思就是，0 和 1 的数量相同，其实完全可以转化为 0->-1 时中间区间为零的最大子数组；在一个就是头部的问题，当一个大前缀为零时，需要找到前面最靠前的小前缀，但是其实它本身就是这时最大的，所以结合下标为次数- 1，我们就需要让 `hash\[0\] = 1`

# 五、位运算

## 1. 位运算性质

1. `&`，用来判断某一位是否是 1
2. `|`，用来将某一位赋值为 1 （其实C++很多框架的传参都是通过这种方式实现的）
3. `^`，除了 `相同数字异或为零，零和任何数字异或的到的都是另一个操作数本身` 这条重要性质，还有一个点就是他等价于无进位相加

> 我们的加法实际上等价于两步
> 
> 1. 无进位相加
> 2. 加上进位
> 
> 以十进制为例，7 + 8 无进位相加的结果是 5，进位是 1，那么结果就是 `5 + 10 = 15`

## 2. 消失的两个数字

给定一个数组，包含从 1 到 N 所有的整数，但其中缺了两个数字。你能在 O(N) 时间内只用 O(1) 的空间找到它们吗？

以任意顺序返回这两个数字均可。

示例 1：**

**输入：**`[1]`
**输出：**`[2,3]`

**示例 2：**

**输入：**`[2,3]`
**输出：**`[1,4]`

```java
class Solution {
	public int[] missingTwo(int[] nums) {
		int n = nums.length;
		int len = n + 2;
		int tmp = 0;
		for(int i = 1; i <= len; i++){
			tmp ^= i;
		}
		for(int i = 0; i < n; i++){
			tmp ^= nums[i];
		}
		int cnt = 0;
		while(((1 << cnt) & tmp) == 0){
			cnt++;
		}
		int a = 0, b = 0;
		for(int i = 0; i < n; i++){
			if(((1 << cnt) & nums[i]) == 0){
				a ^= nums[i];
			}
			else{
				b ^= nums[i];
			}
		}
		for(int i = 1; i <= len; i++){
			if(((1 << cnt) & i) == 0){
				a ^= i;
			}
			else{
				b ^= i;
			}
		}
		int[] ret = new int[2];
		ret[0] = a;
		ret[1] = b;
		return ret;
	}
}
```

这道题的核心思想就是：既然消失的是两个数字，隐含了是不同的数字，那么两个数字至少有一个二进制位是不同的，那么两者异或后一定至少有一位为 1。找到这一位，就可以将所有数字分成两半：一半是这一位为零的，一半是这一位为一的。这样，两个数字也一定是一个这一位为零，一个这一位为1，就可以转化为：找到一个消失的数字

---

## 3. 两整数之和

给你两个整数 `a` 和 `b` ，**不使用** 运算符 `+` 和 `-` ​​​​​​​，计算并返回两整数之和。

**示例 1：**

输入：a = 1, b = 2
输出：3

**示例 2：**

输入：a = 2, b = 3
输出：5

```java
class Solution {
	public int getSum(int a, int b) {
		int ret = a ^ b;
		int carry = (a & b) << 1;
		while(carry != 0){
			a = ret;
			b = carry;
			ret = a ^ b;
			carry = (a & b) << 1;
		}
		return ret;
	}
}
```

以 5 + 7 为例，5 的二进制是 101，7 的二进制是 111，两者无进位相加得到 10，进位是 1010 （对于二进制而言，只有当两个对应二进制位上的数字都为 1 时，才需要进位，因此我们可以将两个数字异或，如果不为零，就说明需要进位，而进位需要我们将进位左移一位后加过去）；依次往复，直到进位为零，此时无进位相加的结果就是最终结果

---

## 4. 只出现一次的数字二

给你一个整数数组 `nums` ，除某个元素仅出现 **一次** 外，其余每个元素都恰出现 三次 。请你找出并返回那个只出现了一次的元素。

你必须设计并实现线性时间复杂度的算法且使用常数级空间来解决此问题。

**示例 1：**

输入：`nums = [2,2,3,2]`
输出：3

**示例 2：**

输入：`nums = [0,1,0,1,0,1,99]`
输出：99

**提示：**

- `1 <= nums.length <= 3 * 104`
- `-231 <= nums[i] <= 231 - 1`
- `nums` 中，除某个元素仅出现 **一次** 外，其余每个元素都恰出现 **三次**

```java
class Solution {
	public int singleNumber(int[] nums) {
		int ret = 0;
		for(int i = 0; i < 32; i++){
			int sum = 0;
			for(int j = 0; j < nums.length; j++){
				if((nums[j] & (1 << i)) != 0){
					sum++;
				}
			}
			if(sum % 3 != 0){
				ret += (1 << i);
			}
		}
		return ret;
	}
}
```

二进制运算，如果没有思路的话，就被目光放到比特位这个维度上。一个数出现三次，就意味他的某一位，要么是零，要么是一
这一位的和要么是三要么是零，那么不考虑那个只出现一次的数，所有数这一位的和加起来就是 3 的倍数。

现在考虑那个只出现一次的数，他的每个比特位要么是零，要么是一。那么如果他的这位是零，所有的数（包括这个只出现一次的数）在该比特为的和就是 3 的倍数；反之如果不是，那么一定是这个只出现一次的数在这一位上是 1 。这样我们就可以一位一位的确定出来这个只出现一次的数