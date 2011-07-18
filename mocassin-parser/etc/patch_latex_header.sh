if [ -z "$1" ]; then
echo "ERROR: The first parameter(output directory) is empty. Exit"
exit
fi
if [ -z "$2" ]; then
echo "ERROR: The second parameter(filename) is empty. Exit"
exit
fi
filename=$(basename $2)

sed 's/\(^\\documentclass\[.*\]*{.*}\)/\1 \n\\usepackage{pdfsync}\n\\usepackage{xcolor}\n\\usepackage{framed}\n\\colorlet{shadecolor}{yellow!30}/;s/\\usepackage\[dvips\]/\\usepackage/' $2 > $1/$filename

