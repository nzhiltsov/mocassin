for file in /opt/mocassin/gate/gate.corpora.DocumentImpl/*; do
rm $file
done
for file in /opt/mocassin/pdfsync/*; do
rm $file
done
for file in /opt/mocassin/pdf/*; do
rm $file
done
for file in /opt/mocassin/aux-pdf/*; do
rm $file
done
for file in /opt/mocassin/arxmliv/*; do
rm $file
done
for file in /opt/mocassin/patched-tex/*; do
filename=$(basename $file)
if [ "$filename" == "patch_latex_shaded.sh" ]; then
:;
else 
rm $file
fi
done
for file in /opt/mocassin/shaded-tex/*; do
rm $file
done