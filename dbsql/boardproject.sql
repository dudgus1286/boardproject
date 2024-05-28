-- 메모용
-- 검색어와 맞는 개별글 찾기
SELECT * FROM 테이블명 WHERE 글ID = 개별글ID;
-- 개별글만 따로 조회하는 함수 만듦

-- 상위글 찾기 위해 별개로 개별글의 정보를 변수 x에 담음
-- 개별글의 인접한 상위 글 찾기
SELECT * FROM 테이블명 WHERE 글ID = x.마지막참조글ID;
-- 인접한 상위글의 정보를 변수 y에 담음
-- 최초글 찾기
SELECT * FROM 테이블명 WHERE 글ID = x.최초글ID;
-- 인접한 상위글의 정보를 변수 z에 담음

-- x,y 와 z 사이의 글이 있을 경우 이것들을 추가해 리스트 만들기

	-- 최초글은 글 고유의 id, 원본참조글id, 마지막참조글id가 모두 동일
	-- 상위글은 글 고유의 id가 원본참조글, 마지막참조글과 동일하지 않음, 이후 달린 댓글의 마지막참조글id와 고유id가 동일함
		-- 상위글의 바로 상위 인접한 글이 최초글일 수도, 다른 상위글일수도 있음
			-- 원본참조글 == 마지막참조글인 경우 최초글 바로 밑에 달린 댓글임
			-- 원본참조글 != 마지막참조글인 경우 이 글또한 다른 글의 댓글임


	-- java로 구현할 때
-- 상위글을 담는 리스트를 만듦, 리스트에 y, z 담음
-- 바로 직전에 조회한 상위글을 담는 변수 n을 만듦(while문을 돌리기 전 값은 y)
-- 미싱링크 판별용 Boolean 변수 b 만듦

-- while 문을 돌려서 조회한 상위글 id와 상위글 원본글이 같아질 때까지 반복
-- 기본구성 : while (n != z) {
	-- a = select * from 테이블명 where 글ID = n.마지막참조글ID
	-- if a == null {
		-- b == false
		-- break;
		-- }
	-- n = a
	-- list.add(n)
	
-- }
	-- 조회한 상위글이 원본글과 다르고 존재한다면 리스트에 추가
	-- 조회한 상위글이 원본글과 다르지만 null 값을 만나게 되면 미싱링크 판별용 Boolean 변수에 결과값을 반영한 후 while문에서 나감
-- 배열을 작성일자별로 재배치

	-- 미싱링크 처리
-- 부모 개체(상위글, 최초글 포함)가 삭제될 경우 자식 개체의 참조글id 값은 null이나 별도로 지정된 값으로 변하고 삭제되지 않음
-- 만약 자식 개체로 상위글 리스트를 조회하려고 할 때 null 값을 만나게 되면 '삭제된 글입니다' 알림 영역 표시
-- 최초글 -- 부모글1 -- (부모글2:삭제됨) -- 부모글3 -- 개별글을 조회하려고 하는 경우 최초글, 부모글3, 개별글만 조회가능

-- 개별글 하나 조회할 때 필요한 것:
	-- (개별글 객체, [개별글에 달린 댓글리스트], [[개별글보다 먼저 올라온 상위글 리스트], 미싱링크 참거짓 판별용 변수])
	-- 을 모두 담는 오브젝트 객체
	-- 웹사이트를 구현할 때는 dto로 변환해야 함

SELECT * FROM POST p WHERE p.LAST_REFERENCE = 61 ORDER BY CREATED_AT ASC ;

-- 개별글을 기준으로 해당 게시글 작성자, 복수의 이미지, 있다면 상위의 글과 그 작성자, 있다면 하위의 글 목록과 그 작성자들을 가지고 오기
-- 게시글과 작성자 찾기

SELECT DISTINCT p.pno, pi2.* FROM post p LEFT JOIN POST_IMAGE pi2 ON pi2.POST_PNO = p.PNO 
ORDER BY pno ASC ;