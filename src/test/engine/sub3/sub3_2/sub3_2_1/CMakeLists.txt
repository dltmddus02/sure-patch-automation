project(
  sub3_2_1
  VERSION 1.0
  DESCRIPTION "sub3_2_1"
)

add_library(
	${PROJECT_NAME} SHARED	
	${SOURCE_FILES}
  	${HEADER_FILES}
)

sub3_2_1 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}
sub3_2_1 sub3_2의 매크로 풀기 : POCO_SUB3_MACRO : ${POCO_SUB3_MACRO}
