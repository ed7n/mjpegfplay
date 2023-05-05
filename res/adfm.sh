#!/usr/bin/env bash

{
  declare -p ews || declare -A ews=([base]="${0%/*}" [exec]="${0}" \
      [name]='Adjacent Duplicate File Mover' \
      [sign]='u0r1 by Brendon, 05/05/2023.')
} &> /dev/null

# Output directory.
readonly ADM_OUT="${0%.*}"'-out'

ADM.die() {
  (( ${#} )) && echo "${@}" 1>&2
  exit 1
}

type 'cmp' &> /dev/null || ADM.die '`cmp` not found.'
type 'mkdir' &> /dev/null || ADM.die '`mkdir` not found.'
type 'mv' &> /dev/null || ADM.die '`mv` not found.'
(( ${#} )) || {
  echo 'Usage: <start> <end> <suffix>'
  exit
}
(( ${#} >= 2 )) || ADM.die 'No end.'
(( ${#} >= 3 )) || ADM.die 'No suffix.'
echo -e "${ews[name]}"' '"${ews[sign]}"'\n\nWorking directory:\n  '"$(pwd)"'
Output directory:\n  '"${ADM_OUT}"
[ -e "${ADM_OUT}" ] && ADM.die 'Exists.'
admIdx="${1}"
echo 'Searching.'
while (( admIdx < ${2} )); do
  cmp -s "${admIdx}""${3}" $(( admIdx + 1 ))"${3}" \
      && admItms+=($(( admSta + 1 ))) && echo '  '$(( admSta + 1 ))"${3}"
  (( admIdx++ ))
done
echo 'Found '"${#admItms[*]}"' duplicate(s).'
(( ${#admItms[*]} )) && {
  echo 'Now moving.'
  mkdir -p "${ADM_OUT}" && {
    for admItm in ${admItms[*]}; do
      mv -t "${ADM_OUT}" "${admItm}""${3}"
    done
    echo 'Done.'
  }
} || echo 'Nothing to move.'
