for file in /opt/mocassin/tex/*; do
filename=$(basename $file)
if [ "$filename" == "patch_latex_header.sh" ]; then
:;
else 
rm $file
fi
done
for file in /opt/mocassin/pdfsync/*; do
rm $file
done
for file in /opt/mocassin/pdf/*; do
rm $file
done
for file in /opt/mocassin/patched-tex/*; do
rm $file
done
for file in /opt/mocassin/aux-pdf/*; do
rm $file
done
for file in /opt/mocassin/arxmliv/*; do
rm $file
done
