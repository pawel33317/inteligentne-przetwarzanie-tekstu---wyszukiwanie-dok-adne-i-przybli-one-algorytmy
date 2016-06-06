s1, s2 = "tigers", "trigger"
prev = [0] * (len(s1) + 1)
print prev
for c2 in s2:
  curr = [0] * (len(s1) + 1)
  for i, c1 in enumerate(s1):
    curr[i + 1] = max(prev[i + 1], curr[i]) if c2 != c1 else prev[i] + 1
  prev = curr
  print prev
