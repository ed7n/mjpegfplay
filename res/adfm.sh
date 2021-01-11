#!/bin/bash

echo
echo

end() {
    echo
    echo
    echo "--------------"
    echo "End of adfm.sh"
}

echo "Adjacent Duplicate File Mover (adfm.sh)"
echo "---------------------------------------"
echo "u0r0 by Brendon, 01/19/2019."
echo
echo

if [[ -e adfm.tmp ]]; then
    read -n 1 -p "<adfm/?> [y] to delete existing adfm.tmp, quit otherwise. " in
    echo

    if [[ "$in" != "y" ]]; then
        echo "<adfm/i> Sorry, but adfm.tmp is required proceed."
        end
        exit 2
    fi
    rm adfm.tmp
fi
read -p "<adfm/?> Input starting file number: " file
read -p "<adfm/?> Input ending file number: " end
read -p "<adfm/?> Input file extension: " ext
echo "<adfm/i> Performing adjacent file comparison..."

while (( $file < $end )); do
    cmp -s $file.$ext $(($file + 1)).$ext

    if [[ $? == 0 ]]; then
        echo $(($file + 1)) >> adfm.tmp
    fi
    ((file++))
done

if ! [[ -e adfm.tmp ]]; then
    echo "<adfm/i> ...done. No duplicates found."
else
    mkdir adfm-duplicates > /dev/null 2>&1
    echo "<adfm/i> ...done. Moving found duplicates..."

    for i in $(cat adfm.tmp); do
        mv $i.$ext adfm-duplicates > /dev/null 2>&1
    done
    rm adfm.tmp
    echo "<adfm/i> ...done. Check ./adfm-duplicates/"
fi
end
