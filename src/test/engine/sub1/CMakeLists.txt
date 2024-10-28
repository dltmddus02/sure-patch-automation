project(
  sub1
  VERSION 1.0
  DESCRIPTION "sub1"
)

add_executable(
	${PROJECT_NAME}
	${SRC_PROJECT}
)

sub1 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}