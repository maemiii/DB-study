# 미니 프로젝트
### Requirements

SQL
- 테이블 2개 이상(+1)
- 외래키 사용
- LEFT JOIN

JAVA
- Named JDBC Template, SimpleJdbcInsert 사용
- 입력은 while(true), Scanner(quit이 입력될 때까지 동작)
- c(INSERT), u(UPDATE), r(단건 READ), ra(목록 READ ALL), d(DELETE)

ex) Department - Employee (1:N)
예시 설명: 하나의 부서(Department)는 여러 명의 직원을 가질 수 있으므로 1:N 관계입니다. 즉, 부서 테이블의 기본키가 직원 테이블의 외래키로 들어가며, 직원은 하나의 부서에만 속합니다.