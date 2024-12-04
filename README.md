# 엔진 바이너리 패치 자동화 프로그램

변경된 소스파일을 참고해 빌드할 모듈을 자동으로 추출하는 프로그래미. 해당 repository를 클론받은 후 사용할 수 있습니다.

## 사용 방법

1. main 브랜치를 클론받아 engine-PatchModuleFinder 프로젝트를 gradle 빌드합니다.
2. engine-PatchModuleFinder/build/libs 경로에 engine-PatchModuleFinder.jar을 다음의 인자들과 실행합니다.
```
- enginePath : 엔진 repository가 존재하는 절대경로를 입력합니다.
- changedSourceFiles : 변경된 소스파일들의 절대경로를 쉼표로 구분해 입력합니다.
```
3. 커맨드의 예시는 다음과 같습니다.
```
java -jar build_engine_patch_finder.jar "-enginePath=C:\01.jenkins\agent\workspace\build_engine_GIT_window" "-changedSourceFiles=GIT_window\src\ut\UnitTest\TestUT\TestUtMain.cpp, src\util\UTIL_LIB\cs_UTIL_hash.c"
```