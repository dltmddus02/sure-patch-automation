project(
  sub2
  VERSION 1.0
  DESCRIPTION "sub2"
)

sub2 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}

add_library(
	${PROJECT_NAME} STATIC	
	${SOURCE_FILES}
  	${HEADER_FILES}
)

add_subdirectory(sub2_1)
