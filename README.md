# COMS4733-Robotics

##  Guideline
* Work on your own branch. For each feature or bug fix, it should create a branch before submitting a patch or pull request. Any request to master or develop will be rejected in the future.
* Branch should follow a naming rule. It should be named as feature/YOUR_FEATURE_NAME or bugfix/YOUR_BUG_NAME.
* Always base on latest develop branch. Use "git pull --rebase" to rewind your changes on top of develop.
* Amend your commit. Prefer merge all your commits into one for one feature or bug fix. (git reset --soft "HEAD^", git commit --amend)

##  Copy files to gopigo
Since gopigo won't be connecting to the network, we can use scp protocl to transfer files on to it. The following example will transfer the whole tmp directory to the gopigo
```
scp -r tmp/ pi@192.168.10.105:/home/pi/COMS4733/
```