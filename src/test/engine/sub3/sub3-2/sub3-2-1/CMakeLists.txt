project(
  sub3-2-1
  VERSION 1.0
  DESCRIPTION "sub3-2-1"
)

add_library(
	${PROJECT_NAME} SHARED	
	${SOURCE_FILES}
  	${HEADER_FILES}
)

sub3-2-1 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}
sub3-2-1 sub3-2의 매크로 풀기 : POCO_SUB3_MACRO : ${POCO_SUB3_MACRO}
