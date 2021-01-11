#!/bin/bash

freezing=0
echo
echo
echo "Freezing Sequence Metadata Maker (makefreeze.sh)"
echo "------------------------------------------------"
echo "u0r0 by Brendon, 01/19/2019."
echo
echo

if [[ -e makefreeze.out ]]; then
    read -n 1 -p "<makefreeze/?> [y] to delete existing makefreeze.out, append otherwise. " in
    echo

    if [[ "$in" == "y" ]]; then
        rm makefreeze.out
    fi
fi
read -p "<makefreeze/?> Input starting point: " start
read -p "<makefreeze/?> Input ending point: " end
read -p "<makefreeze/?> Input file extension: " ext
echo "<makefreeze/i> Generating freezing sequence metadata..."
echo "    freezePoints:" >> makefreeze.out

while (( $start < $end )); do
    if [[ $freezing == 0 ]] && ! [[ -e $(($start + 1)).$ext ]]; then
        echo "$start," >> makefreeze.out
        freezing=1
    elif [[ $freezing == 1 ]] && [[ -e $(($start + 1)).$ext ]]; then
        echo "$(($start + 1))," >> makefreeze.out
        freezing=0
    fi
    ((start++))
done
echo "    :freezePoints    # delete the last comma (,)" >> makefreeze.out
echo "<makefreeze/i> ...done, check ./makefreeze.out"
echo
echo
echo "--------------------"
echo "End of makefreeze.sh"
