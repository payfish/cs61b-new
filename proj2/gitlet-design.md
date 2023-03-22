# Gitlet Design Document

**Name**:Fred
## Notes
1. Compare two blobs' SHA-1 code to see if they are the same
2. list1.addAll(list2): Add a list to another list 
3. When inits a new repository,the file system should look like this
   - objects
      - commits and blobs _(folders)_
   - refs
      - heads
         - master _(file)_
      - tags
   - HEAD
   - STAGE
4. A blob's sha1 code like f5eea678d87a8664e4c76e12d3ef5c4ff775ad58
should be saved like this in the file system:
   - f5 _(folder)_
      - eea678d87a8664e4c76e12d3ef5c4ff775ad58 _(file)_
   
5. Blob object only saves the content of the file without its name
6. A commit file's contents:
   
   - tree 1c0d57fe0c4ba08e686f23454003eaa328e10537
   - author yyuff <ezyyuff@163.com> 1679060562 +0800
   - committer yyuff <ezyyuff@163.com> 1679060562 +0800
   - hello wo    //--this is message

## Classes and Data Structures

### Class 1

#### Fields

1. Field 1
2. Field 2


### Class 2

#### Fields

1. Field 1
2. Field 2


## Algorithms

## Persistence

