#!/usr/bin/env bash

{
  declare -p ews || declare -A ews=([base]="${0%/*}" [exec]="${0}" \
      [name]='Freezing Sequence Metadata Maker' [sign]='u0r1 by Brendon, 05/05/2023.' \
      [desc]='mJPEGfPlay supplement. https://ed7n.github.io/mjpegfplay')
} &> /dev/null

# Output file.
readonly FSM_OUT="${0%.*}"'.txt'

echo -e "${ews[name]}"' '"${ews[sign]}"'\n——'"${ews[desc]}"'\n
Working directory:\n  '"$(pwd)"'\nOutput file:\n  '"${FSM_OUT}"
[ -e "${FSM_OUT}" ] && {
  echo 'Exists.'
  exit 1
}
echo -e '\033]2;'"${ews[name]}"'\007Enter starting point.'
read -p '> ' fsmSta
echo 'Enter ending point.'
read -p '> ' fsmEnd
echo 'Enter extension.'
read -p '> ' fsmExt
admExt='.'"${admExt}"
echo 'Now making.'
echo '    freezePoints:' >> "${FSM_OUT}"
while (( fsmSta < fsmEnd )); do
  (( "${#fsmFrz}" )) && {
    [ -e $(( fsmSta + 1 ))"${fsmExt}" ] && {
      fsmFrz=
      fsmPts+=$(( fsmSta + 1 ))','$'\n'
    } || :
  } || {
    [ -e $(( fsmSta + 1 ))"${fsmExt}" ] || {
      fsmFrz='frz'
      fsmPts+="${fsmSta}"','$'\n'
    }
  }
  (( fsmSta++ ))
done
echo -e "${fsmPts:0:-2}"'\n    :freezePoints' >> "${FSM_OUT}"
echo -e '\033]2;\007Done.'
