project(
  sub3-2
  VERSION 1.0
  DESCRIPTION "sub3-2"
)


sub3-2 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}

set(SUB3MACRO "sub3의 매크로")
set(POCO_SUB3_MACRO "Poco${SUB3MACRO}mt")


add_subdirectory(sub3-2-1)
