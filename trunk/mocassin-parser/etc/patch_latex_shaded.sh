if [ -z "$1" ]; then
echo "ERROR: The first parameter(starting line number) is empty. Exit"
exit
fi
if [ -z "$2" ]; then
echo "ERROR: The second parameter(ending line number) is empty. Exit"
exit
fi
if [ -z "$3" ]; then
echo "ERROR: The third parameter(element id) is empty. Exit"
exit
fi
if [ -z "$4" ]; then
echo "ERROR: The fourth parameter(input filepath) is empty. Exit"
exit
fi
if [ -z "$5" ]; then
echo "ERROR: The fifth parameter(output directory) is empty. Exit"
exit
fi
filename=$(basename $4)
arxivid=${filename%.*}
delimiter="$"
extension=".tex"
output=$5/$arxivid$delimiter$3$extension
startLine="s/^/\\\\begin{shaded}/;"
endLine="s/$/\\\\end{shaded}/"
sedcommand=$1$startLine$2$endLine

sed "$sedcommand" $4 > $output


