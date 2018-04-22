function [inliers_id, H] = runRANSAC(Xs, Xd, ransac_n, eps)

Xs = Xs';Xd = Xd';
minPointNum = 4;
iterNum = ransac_n;
thInlrRatio = 0.1;
thDist = eps;
%thDist = 400000;
pointNum = size(Xs,2);
thInlr = round(thInlrRatio*pointNum);

inlinerNum = zeros(1,iterNum);
fLib = cell(1,iterNum);
%   Define function handle
funcFindF = @solveHomo;
funcDist = @calDist;

for p = 1:iterNum
	% 1. fit using  random points
	sampleIdx = randIndex(pointNum,minPointNum);
	f1 = funcFindF(Xs(:,sampleIdx),Xd(:,sampleIdx));
	
	% 2. count the inliers, if more than thInlr, refit; else iterate
	dist = funcDist(f1,Xs,Xd);
	inlier1 = find(dist < thDist);
	inlinerNum(p) = length(inlier1);
	if length(inlier1) < thInlr, continue; end
	fLib{p} = funcFindF(Xs(:,inlier1),Xd(:,inlier1));
end

% 3. choose the coef with the most inliers
[~,idx] = max(inlinerNum);
H = fLib{idx};
dist = funcDist(H,Xs,Xd);
inliers_id = find(dist < thDist);
	
end