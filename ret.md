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
    int mid = left + (right - left + 1) / 2;
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

# 六、模拟

模拟其实更多考察代码能力，解决方法题目中已经解释了，只需要自己完成实现，重要的是先想好怎么做，怎么把题目中的描述通过代码简单的实现出来，如果一开始的想法难以实现，要么是题目理解的问题，要么就是有更好的解决或者说思考角度。

即：理解题意 -> 代码实现（这里的代码实现，只是为了达成相应效果，不一定意味着完全复现题中描述的思路，如果不好复现，那么就通过方便的方式实现就好了）

---

## 1. 替换所有问号

给你一个仅包含小写英文字母和 `'?'` 字符的字符串 `s`，请你将所有的 `'?'` 转换为若干小写字母，使最终的字符串不包含任何 **连续重复** 的字符。

注意：你 **不能** 修改非 `'?'` 字符。

题目测试用例保证 **除** `'?'` 字符 **之外**，不存在连续重复的字符。

在完成所有转换（可能无需转换）后返回最终的字符串。如果有多个解决方案，请返回其中任何一个。可以证明，在给定的约束条件下，答案总是存在的。

**示例 1：**

输入：s = "?zs"
输出："azs"
解释：该示例共有 25 种解决方案，从 "azs" 到 "yzs" 都是符合题目要求的。只有 "z" 是无效的修改，因为字符串 "zzs" 中有连续重复的两个 'z' 。

**示例 2：**

输入：s = "ubv?w"
输出："ubvaw"
解释：该示例共有 24 种解决方案，只有替换成 "v" 和 "w" 不符合题目要求。因为 "ubvvw" 和 "ubvww" 都包含连续重复的字符。

> 这道题关键在于怎么去保证替换过的问号的字符怎么样和前后的字符不重，要满足的条件有两个：
> 
> 1. 前面是头，或者和前面不一样
> 
> 2. 后面是尾，或者和后面不一样

---

## 2. 提莫攻击

在《英雄联盟》的世界中，有一个叫 “提莫” 的英雄。他的攻击可以让敌方英雄艾希（编者注：寒冰射手）进入中毒状态。

当提莫攻击艾希，艾希的中毒状态正好持续 `duration` 秒。

正式地讲，提莫在 `t` 发起攻击意味着艾希在时间区间 `[t, t + duration - 1]`（含 `t` 和 `t + duration - 1`）处于中毒状态。如果提莫在中毒影响结束 **前** 再次攻击，中毒状态计时器将会 **重置** ，在新的攻击之后，中毒影响将会在 `duration` 秒后结束。

给你一个 **非递减** 的整数数组 `timeSeries` ，其中 `timeSeries[i]` 表示提莫在 `timeSeries[i]` 秒时对艾希发起攻击，以及一个表示中毒持续时间的整数 `duration` 。

返回艾希处于中毒状态的 **总** 秒数。

**示例 1：**

输入：timeSeries = `[1,4]`, duration = 2
输出：4
解释：提莫攻击对艾希的影响如下：
- 第 1 秒，提莫攻击艾希并使其立即中毒。中毒状态会维持 2 秒，即第 1 秒和第 2 秒。
- 第 4 秒，提莫再次攻击艾希，艾希中毒状态又持续 2 秒，即第 4 秒和第 5 秒。
艾希在第 1、2、4、5 秒处于中毒状态，所以总中毒秒数是 4 。

**示例 2：**

输入：timeSeries = `[1,2]`, duration = 2
输出：3
解释：提莫攻击对艾希的影响如下：
- 第 1 秒，提莫攻击艾希并使其立即中毒。中毒状态会维持 2 秒，即第 1 秒和第 2 秒。
- 第 2 秒，提莫再次攻击艾希，并重置中毒计时器，艾希中毒状态需要持续 2 秒，即第 2 秒和第 3 秒。
艾希在第 1、2、3 秒处于中毒状态，所以总中毒秒数是 3 。

> 这道题的关键在于，怎么去判断中毒时间，是完整的一段，还是被下一次中毒打断，所以最简单的方法是：如果是最后一次攻击，那么就是完整的中毒时间；如果不是最后一次，那么就计算和下一次攻击时间的间隔，取间隔和中毒事件的最小值

---

## 3. 数青蛙

给你一个字符串 `croakOfFrogs`，它表示不同青蛙发出的蛙鸣声（字符串 `"croak"` ）的组合。由于同一时间可以有多只青蛙呱呱作响，所以 `croakOfFrogs` 中会混合多个 `“croak”` _。_

请你返回模拟字符串中所有蛙鸣所需不同青蛙的最少数目。

要想发出蛙鸣 "croak"，青蛙必须 **依序** 输出 `‘c’, ’r’, ’o’, ’a’, ’k’` 这 5 个字母。如果没有输出全部五个字母，那么它就不会发出声音。如果字符串 `croakOfFrogs` 不是由若干有效的 "croak" 字符混合而成，请返回 `-1` 。

**示例 1：**

输入：croakOfFrogs = "croakcroak"
输出：1 
解释：一只青蛙 “呱呱” 两次

**示例 2：**

输入：croakOfFrogs = "crcoakroak"
输出：2 
解释：最少需要两只青蛙，“呱呱” 声用黑体标注
第一只青蛙 "**cr**c**oak**roak"
第二只青蛙 "cr**c**oak**roak**"

**示例 3：**

输入：croakOfFrogs = "croakcrook"
输出：-1
解释：给出的字符串不是 "croak" 的有效组合。

> 数青蛙关键在于怎样判断每只青蛙“叫到了哪里”，或者说我们并不关系具体某一只青蛙的情况，我们在意的是叫到这个位置的青蛙的数目，我们通过一个数组代表每个字母，每当叫到一个位置时，就需要 croak 中上一个位置的青蛙来到当前位置，如果来不了（上个位置没有青蛙）那么就说明这个字符串数不出来青蛙；当出现 `c` （开头）时，我们就需要开始新的一轮，首先从 k 中轮换过来一只已经叫完的，如果没有，那么就需要再找来一只了

# 七、分治_快排

## 7.1 颜色划分

给定一个包含红色、白色和蓝色、共 `n` 个元素的数组 `nums` ，**[原地](https://baike.baidu.com/item/%E5%8E%9F%E5%9C%B0%E7%AE%97%E6%B3%95)** 对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。

我们使用整数 `0`、 `1` 和 `2` 分别表示红色、白色和蓝色。

必须在不使用库内置的 sort 函数的情况下解决这个问题。

**示例 1：**

输入：nums =` [2,0,2,1,1,0]`
输出：`[0,0,1,1,2,2]`

**示例 2：**

输入：nums = `[2,0,1]`
输出：`[0,1,2]`

---

这道题使用可以采用类似于双指针的“三指针”解法。原因就是在任意处理过程中，都可以将数组划分为四个部分：

```plaintext
0 ... 0 1 ... 1 [unhandled] 2 ... 2
    left        i         right
```

```java
class Solution {
	public void sortColors(int[] nums) {
		int n = nums.length;
		int left = -1, right = n;
		for(int i = 0; i < right;){
			if(nums[i] == 0){
				swap(nums, ++left, i++);
			}
			else if(nums[i] == 1){
				i++;
			}
			else{
				swap(nums, --right, i);
			}
		}
	}
	void swap(int[] nums, int left, int right){
		int tmp = nums[left];
		nums[left] = nums[right];
		nums[right] = tmp;
	}
}
```

# 八、分治_归并

归并可以用来解决在一个数组中解决若干个符合某种要求的数对。在递归中，首先要有黑盒的思想；其次，对于这个数组来说，我们相信全部落在左区间或右区间的数组都已经被处理了，只有分别落在数对左右区间的数对需要统计。对于这样的区间，其在相应区间里的顺序并不会影响数对的统计，所以我们就可以利用这种“顺序无关性”帮助我们进行统计

结合归并排序的特点，在往buffer放元素时，可能会出现额外单放的情况，在这时需要通过排序顺序的选择来减轻逻辑复杂度

---

## 8.1 翻转对

给定一个数组 `nums` ，如果 `i < j` 且 `nums[i] > 2*nums[j]` 我们就将 `(i, j)` 称作一个重要翻转对。

你需要返回给定数组中的重要翻转对的数量。

**示例 1:**

**输入**: `[1,3,2,3,1]`
**输出**: 2

```java
class Solution {
	private int[] arr;
	int ret = 0;
	public int reversePairs(int[] record) {
		int n = record.length;
		arr = new int[n];
		mergeSort(record, 0, n - 1);
		return ret;
	}
	
	void countPair(int[] nums, int left, int lend, int right, int rend){
		while(left <= lend && right <= rend){
			if(nums[left] > nums[right]){
				ret += rend - right + 1;
				left++;
			}
			else{
				right++;
			}
		}
	}
	int mergeSort(int[] nums, int begin, int end){
		if(begin >= end){
			return 0;
		}
		int mid = (begin + end) / 2;
		mergeSort(nums, begin, mid);
		mergeSort(nums, mid + 1, end);
		countPair(nums, begin, mid, mid + 1, end);
		int left = begin, right = mid + 1;
		int tmp = begin;
		while(left <= mid && right <= end){
			if(nums[left] <= nums[right]){
				arr[tmp++] = nums[right++];
			}
			else{
				arr[tmp++] = nums[left++];
			}
		}
		while(left <= mid){
			arr[tmp++] = nums[left++];
		}
		while(right <= end){
			arr[tmp++] = nums[right++];
		}
		for(int i = begin; i <= end; i++){
			nums[i] = arr[i];
		}
		return ret;
	}
}
```

这题就是完美体现归并用处

---
## 8.2 计算右侧小于当前元素的个数

给你一个整数数组 `nums` ，按要求返回一个新数组 `counts` 。数组 `counts` 有该性质： `counts[i]` 的值是  `nums[i]` 右侧小于 `nums[i]` 的元素的数量。

**示例 1：**

输入：nums = `[5,2,6,1]`
输出：`[2,1,1,0]`  

**解释：**
5 的右侧有 **2** 个更小的元素 (2 和 1)
2 的右侧仅有 **1** 个更小的元素 (1)
6 的右侧有 **1** 个更小的元素 (1)
1 的右侧有 **0** 个更小的元素

```java
class Solution {
	private int[] arr;
	private int[] ret;
	private int[] index_tmp;
	private int[] index;
	public List<Integer> countSmaller(int[] nums) {
		int n = nums.length;
		arr = new int[n];
		ret = new int[n];
		index_tmp = new int[n];
		index = new int[n];
		for(int i = 0; i < n; i++){
			index[i] = i;
		}
		mergeSort(nums, 0, n - 1);
		List<Integer> ret_list = new ArrayList<>();
		for(int i = 0; i < n; i++){
			ret_list.add(ret[i]);
		}
		return ret_list;
	}
	
	void mergeSort(int[] nums, int begin, int end){
		if(begin >= end){
			return;
		}
		int mid = (begin + end) / 2;
		mergeSort(nums, begin, mid);
		mergeSort(nums, mid + 1, end);
		int left = begin, right = mid + 1;
		int tmp = begin;
		while(left <= mid && right <= end){
			if(nums[left] <= nums[right]){
				arr[tmp] = nums[right];
				index_tmp[tmp] = index[right];
				tmp++; right++;
			}
			else{
				ret[index[left]] += end - right + 1;
				index_tmp[tmp] = index[left];
				arr[tmp] = nums[left];
				tmp++; left++;
			}
		}
		while(left <= mid){
			arr[tmp] = nums[left];
			index_tmp[tmp] = index[left];
			tmp++; left++;
		}
		while(right <= end){
			arr[tmp] = nums[right];
			index_tmp[tmp] = index[right];
			tmp++; right++;
		}
		for(int i = begin; i <= end; i++){
			nums[i] = arr[i];
			index[i] = index_tmp[i];
		}
	}
}
```

关键在于归并排序会导致下标错位，所以需要一个额外的数组在排序时保存下标的映射关系，同时需要在所有对原数组操作的同时对下标数组也做出对等的操作，保证位置的同步。至于每个位置结果的存放，就需要通过下标数组找到对应的原始下标再记录结果了

---

# 九、哈希表

1. 哈希表不一定是标准库提供的容器，也可以是通过位图、整形数组的方式代替，尤其是只有小写字母或者数据范围固定的情况下

2. 哈希表的最主要目的就是映射（优化遍历查找）

## 9.1 字母异位词分组

给你一个字符串数组，请你将 字母异位词 组合在一起。可以按任意顺序返回结果列表。

```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> rets = new ArrayList<>();
        HashMap<String, List<String>> hash = new HashMap<>();
        for(int i = 0; i < strs.length; i++){
            char[] tmp = strs[i].toCharArray();
            Arrays.sort(tmp);
            String s = new String(tmp);
            if(hash.containsKey(s)){
                hash.get(s).add(strs[i]);
            }
            else{
                List<String> item = new ArrayList<>();
                hash.put(s, item);
                item.add(strs[i]);
            }
        }
        rets.addAll(hash.values());
        return rets;
    }
}
```

把这道题选进来，就是这里对于异位词的处理：对于异位词而言，其所含字母的种类和数量都是相同的，所以排序之后得到的字符串也是相同的，因此他们可以被映射到同一个String数组中

# 十、字符串

这里其实没有什么固定的思想或者说套路，大多通过类似模拟的方式就可以解决了（但模拟的对象是自己的解法）

## 10.1 最长回文子串

给你一个字符串 s，找到 s 中最长的 回文 子串。

示例 1：

输入：s = "babad"
输出："bab"
解释："aba" 同样是符合题意的答案。

```java
class Solution {
    public String longestPalindrome(String s) {
        int n = s.length();
        String ret = "";
        for(int i = 0; i < n; i++){
            int left = i;
            int right = left;
            while(left >= 0 && right <= n - 1 && s.charAt(left) == s.charAt(right)){
                left--;
                right++;
            }
            int tmp = right - left - 1;
            if(tmp > ret.length()){
                ret = s.substring(left + 1, right);
            }
            if(i == n - 1){
                break;
            }
            right = i + 1;
            left = i;
            while(left >= 0 && right <= n - 1 && s.charAt(left) == s.charAt(right)){
                left--;
                right++;
            }
            if(left + 1 == right && s.charAt(left) != s.charAt(right)){
                continue;
            }
            tmp = right - left - 1;
            if(tmp > ret.length()){
                ret = s.substring(left + 1, right);
            }
        }
        return ret;
    }
}
```

选这道题主要是为了介绍“中心拓展算法”。回文串具有一个中心（奇数长度是一个字符，偶数长度是两个相同字符），基于此我们就可以去遍历每一个中心，并记录得到的最长得回文串

---

## 10.2 字符串相乘

给定两个以字符串形式表示的非负整数 num1 和 num2，返回 num1 和 num2 的乘积，它们的乘积也表示为字符串形式。

```java
class Solution {
    public String multiply(String num1, String num2) {
        if(num1.equals("0") || num2.equals("0")){
            return "0";
        }
        int m = num1.length(),  n = num2.length();
        int[] arr = new int[m + n - 1];
        StringBuilder sb1 = new StringBuilder(num1);
        StringBuilder sb2 = new StringBuilder(num2);
        num1 = sb1.reverse().toString();
        num2 = sb2.reverse().toString();
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                int val1 = num1.charAt(i) - '0';
                int val2 = num2.charAt(j) - '0';
                arr[i + j] += val1 * val2;
            }
        }
        StringBuilder ret = new StringBuilder();
        int tmp = 0;
        for(int i = 0; i < arr.length; i++){
            int val = arr[i] + tmp;
            int num = val % 10;
            tmp = val / 10;
            ret.append(num + "");
        }
        if(tmp != 0){
            ret.append(tmp + "");
        }
        return ret.reverse().toString();
    }
}
```

首先肯定是可以模拟竖式运算来完成的，但是太麻烦。对于乘法，我们可以使用无进制相乘，具体过程为：和竖式运算一样，每一位对应相乘，但是每一位上不写个位数字，而是直接写这个运算结果，每一位上也不进位

```text
123 * 456 
      1 2 3 
      4 5 6 
    --------
      6 12 18
    5 10 15
  4 8 12
    ---------
  5 6 0 8 8
```


---

# 十一、栈

## 11.1 基本计算器

给你一个字符串表达式 s ，请你实现一个基本计算器来计算并返回它的值。

整数除法仅保留整数部分。

你可以假设给定的表达式总是有效的。所有中间结果将在 [-231, 231 - 1] 的范围内。

```java
class Solution {
    private int i = 0;
    public int calculate(String s) {
        int n = s.length();
        int ret = 0;
        char op = '+';
        Stack<Integer> st = new Stack<>();
        for(; i < n;){
            char ch = s.charAt(i);
            if(ch == ' '){
                i++;
                continue;
            }
            if(ch >= '0' && ch <= '9'){
                int val = getNum(s);
                if(op == '+'){
                    st.add(val);
                }
                else if(op == '-'){
                    st.add(0 - val);
                }
                else if(op == '*'){
                    int val2 = st.peek();
                    st.pop();
                    st.add(val2 * val);
                }
                else{
                    int val2 = st.peek();
                    st.pop();
                    st.add(val2 / val);
                }
            }
            else{
                if(ch == '+'){
                    op = '+';
                }
                else if(ch == '-'){
                    op = '-';
                }
                else if(ch == '*'){
                    op = '*';
                }
                else{
                    op = '/';
                }
                i++;
            }
        }
        while(!st.isEmpty()){
            int val = st.peek();
            st.pop();
            ret += val;
        }
        return ret;
    }

    public int getNum(String s){
        //1234
        int ret = 0;
        while(i < s.length() && s.charAt(i) >= '0' && s.charAt(i) <= '9'){
            ret *= 10;
            ret += s.charAt(i) - '0';
            i++;
        }
        return ret;
    }
}
```

这道题我们的处理思路是：把所有乘除法、减法都处理为结果，最后通过加法的方式获得最终结果。这时，栈的作用就出现了：栈可以让我们始终取到最近的若干个元素，而我们每次要进行乘法、除法的时候需要获取的都是最前面的**一个**数。

---

## 11.2 字符串解码

给定一个经过编码的字符串，返回它解码后的字符串。

编码规则为: k`[encoded_string]`，表示其中方括号内部的 encoded_string 正好重复 k 次。注意 k 保证为正整数。

你可以认为输入字符串总是有效的；输入字符串中没有额外的空格，且输入的方括号总是符合格式要求的。

此外，你可以认为原始数据不包含数字，所有的数字只表示重复的次数 k ，例如不会出现像 3a 或 2`[4]` 的输入。

测试用例保证输出的长度不会超过 105。

示例 1：

输入：s = "`3[a]2[bc]`"
输出："aaabcbc"
示例 2：

输入：s = "`3[a2[c]]`"
输出："accaccacc"
示例 3：

输入：s = "`2[abc]3[cd]ef`"
输出："abcabccdcdcdef"

```java
class Solution {
    int index = 0;
    String s = "";
    int n = 0;
    String ret = "";
    public String decodeString(String str) {
        s = str;
        n = str.length();
        Stack<Integer> st_num = new Stack<>();
        Stack<String> st_str = new Stack<>();
        st_str.add("");
        while(index < n){
            char ch = str.charAt(index);
            if(ch == '['){
                index++;
                String tmp = getStr();
                st_str.add(tmp);
            }
            else if(ch == ']'){
                int cnt = st_num.peek();
                st_num.pop();
                String tmp = st_str.peek();
                st_str.pop();
                StringBuilder sb = new StringBuilder(st_str.peek());
                st_str.pop();
                while(cnt-- != 0){
                    sb.append(tmp);
                }
                st_str.add(sb.toString());
                index++;
            }
            else if(ch >= '0' && ch <= '9'){
                st_num.add(getNum());
            }
            else{
                String tmp = getStr();
                StringBuilder sb = new StringBuilder(st_str.peek());
                st_str.pop();
                sb.append(tmp);
                st_str.add(sb.toString());
            }
        }
        return st_str.peek();
    }

    private int getNum(){
        int ret = 0;
        while(index < n && s.charAt(index) >= '0' && s.charAt(index) <= '9'){
            ret *= 10;
            ret += s.charAt(index) - '0';
            index++;
        }
        return ret;
    }

    private String getStr(){
        StringBuilder sb = new StringBuilder();
        while(index < n && s.charAt(index) >= 'a' && s.charAt(index) <= 'z'){
            sb.append(s.charAt(index));
            index++;
        }
        return sb.toString();
    }
}
```

这个题算是栈的题目里很复杂的题目了，我们的思路是：从内到外完成解析（这一部分通过栈来保证），如果遇到数字那么就保存到数字的栈里；如果遇到了`[`，那么接下来跟的一定是准备开始解析的新的一段字符串（放到栈顶的），如果是`]`，就说明当前栈顶的字符串的解析已经完成了一部分（具备了解析字段的重复部分和次数），就需要把内容追加到站定元素后面；如果是直接遇到了字符串，那么这部分是对于当前栈顶字符串来说不需要重复的部分，直接添加的栈顶字符串中（因为另一种出现字符，即遇到 `[` 的下一个位置，已经被我们在 `[` 的地方处理了）

另外在我们的方法里，当前解析的内容最后都放到了栈顶字符串的下一个中，所有在开始解析字符串以前首先往栈里添加一个空串，用来存放最终结果

# 十二、二叉树的层序遍历

## 12.1 二叉树的最大宽度

给你一棵二叉树的根节点 root ，返回树的 最大宽度 。

树的 最大宽度 是所有层中最大的 宽度 。

每一层的 宽度 被定义为该层最左和最右的非空节点（即，两个端点）之间的长度。将这个二叉树视作与满二叉树结构相同，两端点间会出现一些延伸到这一层的 null 节点，这些 null 节点也计入长度。

```java
class Solution {
    public class Pair{
        TreeNode node;
        int index;
        Pair(TreeNode node, int index){
            this.node = node;
            this.index = index;
        }
    }
    public int widthOfBinaryTree(TreeNode root) {
        if (root == null) {
            return 0;
        }
        long ret = 0;
        List<Pair> queue = new ArrayList<>();
        Pair pair = new Pair(root, 0);
        queue.add(pair);
        while(!queue.isEmpty()){
            int cnt = queue.size();
            int leftI = queue.get(0).index;
            int rightI = queue.get(cnt - 1).index;
            ret = Math.max(ret, rightI - leftI + 1);

            List<Pair> tmp = new ArrayList<>();
            for(Pair p: queue){
                TreeNode node = p.node;
                int index = p.index;
                if(node.left != null){
                    tmp.add(new Pair(node.left, index * 2 + 1));
                }
                if(node.right != null){
                    tmp.add(new Pair(node.right, index * 2 + 2));
                }
            }
            queue = tmp;
        }
        return (int)ret;
    }
}
```

选这道题更多是为了展现 Java 算法题中数据结构的使用思路。在 Java 中，使用各种数据结构作为存储数据的容器时，C++中的容器适配器的思想就更加明显。比如我们这里通过把节点和下标绑定（C++中我们可以直接使用 std::pair，但是Java中我们只能手搓了），同时我们又需要随机访问“队列”每一层的起止 Pair，所以我们使用ArrayList来代替队列；同时我们又需要把“队列”提供给下一层的遍历，所以我们采取临时容器的方式，完成一层的“入队列”操作后，就用临时队列代替原队列

# 十三、堆

## 13.1 关于大小根堆的自定义比较器

### 13.1.1 Java

Java 中，堆的自定义比较器规则和排序算法的一样，都是返回 e1 - e2，则排升序（或者是等价写法 `e1.compareTo(e2)`），对于堆来说，就是排小根堆（Java默认）；返回 `e2 - e1`，则排降序（或者是等价写法 `e2.compareTo(e1)`），对于堆来说，就是排大根堆

### 13.1.2 C++

C++ 中，堆的自定义比较器规则和排序算法的相反。

对于排序算法，可以简记：看大小于号的上半部分，如果是上升的，例如 `return e1 < e2;`，那么就是排升序；如果是下降的，例如 `return e1 > e2`，那么就是排降序

对于堆来说，规则正好相反，依然看大小于号的上半部分：如果是上升的，例如 `return e1 < e2`，那么排的就是层与层之间的降序关系，也就是大根堆（C++默认），如果是下降的，例如 `return e1 > e2`，那么排的就是层级之间的升序关系，也就是小根堆

另外，对于 stl 中提供的两个比较器，`std::greater<>` 和 `std::less<>` ，分别对应 `return e1 > e2` 和 `return e1 < e2`，如果没有自定义比较器的需求（如实现结构体之间的比较规则），可以等价使用

---

## 13.2 前 k 个高频单词

给定一个单词列表 words 和一个整数 k ，返回前 k 个出现次数最多的单词。

返回的答案应该按单词出现频率由高到低排序。如果不同的单词有相同出现频率， 按字典顺序 排序。

示例 1：

输入: words = `["i", "love", "leetcode", "i", "love", "coding"]`, k = 2
输出: `["i", "love"]`
解析: "i" 和 "love" 为出现次数最多的两个单词，均为2次。
    注意，按字母顺序 "i" 在 "love" 之前。

```java
class Solution {
    private class Pair {
        String word;
        int cnt;

        Pair(String word, int cnt) {
            this.word = word;
            this.cnt = cnt;
        }
    }

    public List<String> topKFrequent(String[] words, int k) {
        int n = words.length;
        List<String> rets = new ArrayList<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>((Pair p1, Pair p2) -> {
            if (p1.cnt == p2.cnt) {
                return p2.word.compareTo(p1.word);
            } else {
                return p1.cnt - p2.cnt;
            }
        });

        HashMap<String, Pair> hash = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            if (hash.containsKey(words[i])) {
                hash.get(words[i]).cnt++;
            } else {
                hash.put(words[i], new Pair(words[i], 1));
            }
        }

        for (Pair p : hash.values()) {
            pq.add(p);
            if (pq.size() > k) {
                pq.poll();
            }
        }

        while (!pq.isEmpty()) {
            rets.add(pq.poll().word);
        }
        Collections.reverse(rets);
        return rets;
    }
}
```

堆的算法题里，主要涉及两种知识点：

1. 自定义比较器的设计 
2. 堆的设计 

在这道题中，选出前 k 个大元素，最简单的做法就是让我们的堆是一个小根堆，并且大小始终不超过 k，只要超过了k，就把堆顶的元素删除（堆顶的元素在小根堆中是最小的，这样不断筛选出堆中的最小元素，最后，所有元素都进入过堆之后，堆中剩下的元素就是前 k 大的元素）

---

## 13.3 数据流的中位数

中位数是有序整数列表中的中间值。如果列表的大小是偶数，则没有中间值，中位数是两个中间值的平均值。

例如 arr = `[2,3,4]` 的中位数是 3 。
例如 arr = `[2,3]` 的中位数是 (2 + 3) / 2 = 2.5 。
实现 MedianFinder 类:

MedianFinder() 初始化 MedianFinder 对象。

void addNum(int num) 将数据流中的整数 num 添加到数据结构中。

double findMedian() 返回到目前为止所有元素的中位数。与实际答案相差 10-5 以内的答案将被接受。

示例 1：

输入
`["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]`
`[[], [1], [2], [], [3], []]`
输出
`[null, null, null, 1.5, null, 2.0]`

```java
class MedianFinder {
    private PriorityQueue<Integer> pq_br;
    private PriorityQueue<Integer> pq_sr;
    //1 6 4 2 6 2 
    public MedianFinder() {
        pq_br = new PriorityQueue<>(Collections.reverseOrder());
        pq_sr = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        int m = pq_br.size();
        int n = pq_sr.size();
        if(m == n){
            if(m == 0 || pq_br.peek() >= num){
                pq_br.add(num);
            }
            else{
                pq_sr.add(num);
                pq_br.add(pq_sr.peek());
                pq_sr.poll();
            }
        }
        else{ //m == n + 1
            if(pq_br.peek() >= num){
                pq_br.add(num);
                pq_sr.add(pq_br.peek());
                pq_br.poll();
                
            }
            else{
                pq_sr.add(num);
            }
        }
    }
    
    public double findMedian() {
        int m = pq_br.size();
        int n = pq_sr.size();
        if(m == n){
            return (pq_br.peek() + pq_sr.peek()) / 2.0;
        }
        else{
            return pq_br.peek();
        }
    }
}
```

这道题我们采取两个堆结合的方式来寻找中位数。一个是大根堆，用来存中位数及比它小的元素；一个是小根堆，用来存比中位数大的元素

---

## 十四、BFS

### 14.1 被围绕的区域

给你一个 m x n 的矩阵 board ，由若干字符 'X' 和 'O' 组成，捕获 所有 被围绕的区域：

连接：一个单元格与水平或垂直方向上相邻的单元格连接。
区域：连接所有 'O' 的单元格来形成一个区域。
围绕：如果一个区域中的所有 'O' 单元格都不在棋盘的边缘，则该区域被包围。这样的区域 完全 被 'X' 单元格包围。
通过 原地 将输入矩阵中的所有 'O' 替换为 'X' 来 捕获被围绕的区域。你不需要返回任何值。

这道题就不写代码了，选这道题的主要原因是强调**正难则反**的思路。如果直接找整个区域中的 `O` 并判断区域是否全部在非边界上的话，写的比较麻烦，需要用额外的数据结构保存每个区域中的位置，并判断是否需要反转。

更好的做法是直接遍历边界，把所有在边界上能通过 bfs 遍历到的 `O` 都设置为其他符号（比如 `.`），最后再恢复就好了

## 十五、BFS解决最短路问题

通过bfs解决最短路问题，就是通过类似落到水面的水滴激起的波纹一样，向四周暴力查找，最先找到的路径就是最短路径

---

### 15.1 为高尔夫比赛砍树

你被请来给一个要举办高尔夫比赛的树林砍树。树林由一个 m x n 的矩阵表示， 在这个矩阵中：

0 表示障碍，无法触碰
1 表示地面，可以行走
比 1 大的数 表示有树的单元格，可以行走，数值表示树的高度
每一步，你都可以向上、下、左、右四个方向之一移动一个单位，如果你站的地方有一棵树，那么你可以决定是否要砍倒它。

你需要按照树的高度从低向高砍掉所有的树，每砍过一颗树，该单元格的值变为 1（即变为地面）。

你将从 (0, 0) 点开始工作，返回你砍完所有树需要走的最小步数。 如果你无法砍完所有的树，返回 -1 。

可以保证的是，没有两棵树的高度是相同的，并且你至少需要砍倒一棵树。

```java
class Solution {
    private int m = 0, n = 0;
    private static int[] dx = {1, -1, 0, 0};
    private static int[] dy = {0, 0, 1, -1};
    public int cutOffTree(List<List<Integer>> forest) {
        m = forest.size(); n = forest.get(0).size();
        List<int[]> trees = new ArrayList<>();

        for(int x = 0; x < m; x++){
            for(int y = 0; y < n; y++){
                if(forest.get(x).get(y) != 0){
                    int[] t1 = {x, y};
                    trees.add(t1);
                }
            }
        }

        trees.sort((int[] i1, int[] i2)->{
            return forest.get(i1[0]).get(i1[1]) - forest.get(i2[0]).get(i2[1]);
        });
        int ret = 0;
        int[] prev = {0, 0};

        for(int i = 0; i < trees.size(); i++){
            if(forest.get(trees.get(i)[0]).get(trees.get(i)[1]) == 1){
                continue;
            }
            int tmp = bfs(forest, prev, trees.get(i));
            prev = trees.get(i);
            if(tmp == -1){
                return -1;
            }
            ret += tmp;
        }
        
        return ret;
    }

    public int bfs(List<List<Integer>> forest, int[] start, int[] end){
        Queue<int[]> q = new LinkedList<>();
        if(start[0] == end[0] && start[1] == end[1]){
            return 0;
        }
        boolean[][] vis = new boolean[m][n];
        q.add(start);
        int ret = 0;
        while(!q.isEmpty()){
            ret++;
            int sz = q.size();
            while(sz-- != 0){
                int[] t1 = q.poll();
                if(vis[t1[0]][t1[1]] == true){
                    continue;
                }
                vis[t1[0]][t1[1]] = true;
                for(int k = 0; k < 4; k++){
                    int a = t1[0] + dx[k], b = t1[1] + dy[k];
                    if(a >= 0 && a < m && b >= 0 && b < n && forest.get(a).get(b) != 0 && !vis[a][b]){
                        if(forest.get(a).get(b) == forest.get(end[0]).get(end[1])){
                            return ret;
                        }
                        int[] t2 = {a, b};
                        q.add(t2);
                    }
                }
            }
        }
        return -1;
    }
}
```

这道题主要有两点需要注意

1. 自定义比较器的使用。

2. 题中已经说明，每棵树高度不会重复，并且要从低到高砍树，那么我们砍树的顺序其实就是固定的。这意味着，这个问题可以转化为若干个最短路径问题
