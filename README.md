# 엔진 바이너리 패치 자동화 프로그램

변경된 소스파일을 참고해 빌드할 모듈을 자동으로 추출하는 프로그래미. 해당 repository를 클론받은 후 사용할 수 있습니다.

## 사용 방법

1. main 브랜치를 클론받아 engine-PatchModuleFinder 프로젝트를 gradle 빌드합니다.
```
cd ${프로젝트 repository 경로}
gradle clean build -x test
```
2. 빌드된 `engine-PatchModuleFinder/build/libs` 경로에 `engine-PatchModuleFinder.jar`을 다음의 인자들과 실행합니다.
```
- enginePath : 엔진 repository가 존재하는 절대경로
- changedSourceFiles : 변경된 소스파일들의 절대경로를 쉼표로 구분한 입력
```
3. 커맨드의 예시는 다음과 같습니다.
```
java -jar build_engine_patch_finder.jar "-enginePath=C:\01.jenkins\agent\workspace\build_engine_GIT_window" "-changedSourceFiles=src\util\UTIL_LIB\cs_UTIL_hash.c, src\ut\COMMON\Args\Args.cpp, src\util\POCO_LIB\Foundation\zlib"
```
4. 실행 결과
changedSourceFiles이 영향을 미치는 엔진 바이너리들이 Set\<String> 형태로 리턴됩니다.
```
changedSourceFiles = "src\util\UTIL_LIB\cs_UTIL_hash.c", 
"src\ut\COMMON\Args\Args.cpp", 
"src\util\POCO_LIB\Foundation\zlib"
```
에 대한 결과
```
[TestExecutor, TestUTCP, TestUCLI, cop, IntegrationTest, TestrunBuilderTest, TestUT, TestRemoteUtil, CoverageRecalculator, tce, TestTinyRunner, TestProjectImporter, Foundation, MessageCodeExtractor, rcli, TestrunBuilder, TestCOP, UCLIDriver, TestExecutorTest]
```

## gradle 설치 버전 안내

현재 해당 프로그램은 Gradle 8.10.2, JDK 17을 사용합니다.
아래 명령어를 입력해서 gradle이 정상적으로 설치되었는지 확인할 수 있습니다.
```
gradle -v
```