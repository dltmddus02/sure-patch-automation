project(
  sub1
  VERSION 1.0
  DESCRIPTION "sub1"
)

add_executable(
	${PROJECT_NAME}
	a.cpp a.h
)

if(UNIX)
target_link_libraries(${PROJECT_NAME} PUBLIC		
    sub4
)
endif()

sub1 root의 매크로 풀기 : POCO_MACRO : ${POCO_MACRO}