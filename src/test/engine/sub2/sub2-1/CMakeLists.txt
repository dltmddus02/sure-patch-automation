project(
  sub2-1
  VERSION 1.0
  DESCRIPTION "sub2-1"
)

add_library(
	${PROJECT_NAME} STATIC	
	${SOURCE_FILES}
  	${HEADER_FILES}
)

sub2-1 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}