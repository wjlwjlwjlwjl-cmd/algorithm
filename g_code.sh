#! /bin/bash
ls
echo "================================"
echo "输入题目文件夹>>"
read dir
ls $dir > /dev/null 2>&1
if [ $(echo $?) -ne 0 ]
then
	mkdir $dir
	echo "创建了新的文件夹"
fi

echo "================================"
echo "输入题目名称（英文大驼峰）>>"
read name
echo "================================"

cd $dir

cat > $name.java <<EOF
package $dir;
public class $name{
    public static void main(){

    }
}

class Solution {
}
EOF

echo "创建完成"
