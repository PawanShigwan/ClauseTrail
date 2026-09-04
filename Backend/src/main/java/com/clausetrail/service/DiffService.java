package com.clausetrail.service;

import com.clausetrail.dto.DiffResponse;
import com.clausetrail.dto.DiffSegmentDTO;
import com.clausetrail.dto.VersionResponse;
import com.clausetrail.model.ChangeType;
import com.clausetrail.model.Clause;
import com.clausetrail.model.ClauseChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiffService {

    private static final Logger log = LoggerFactory.getLogger(DiffService.class);

    public DiffResponse computeDiff(
            String contractId,
            String contractTitle,
            VersionResponse v1,
            VersionResponse v2
    ) {
        List<DiffResponse.ClauseDiffDTO> clauseDiffs = new ArrayList<>();
        int additionsCount = 0;
        int deletionsCount = 0;

        Map<String, Clause> v1Map = new LinkedHashMap<>();
        if (v1.getClauses() != null) {
            for (Clause c : v1.getClauses()) {
                String key = c.getId() != null ? c.getId() : c.getClauseNumber();
                v1Map.put(key, c);
            }
        }

        Map<String, Clause> v2Map = new LinkedHashMap<>();
        if (v2.getClauses() != null) {
            for (Clause c : v2.getClauses()) {
                String key = c.getId() != null ? c.getId() : c.getClauseNumber();
                v2Map.put(key, c);
            }
        }

        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(v1Map.keySet());
        allKeys.addAll(v2Map.keySet());

        int changedClauses = 0;

        for (String key : allKeys) {
            Clause c1 = v1Map.get(key);
            Clause c2 = v2Map.get(key);

            if (c1 != null && c2 != null) {
                // Modified or Unchanged
                boolean modified = !Objects.equals(c1.getContent(), c2.getContent()) || !Objects.equals(c1.getTitle(), c2.getTitle());
                List<DiffSegmentDTO> segments = computeWordDiff(c1.getContent(), c2.getContent());

                for (DiffSegmentDTO seg : segments) {
                    if (seg.getType() == ChangeType.ADDED) additionsCount++;
                    if (seg.getType() == ChangeType.DELETED) deletionsCount++;
                }

                if (modified) changedClauses++;

                ClauseChange changeSummary = ClauseChange.builder()
                        .clauseId(c2.getId())
                        .clauseNumber(c2.getClauseNumber())
                        .clauseTitle(c2.getTitle())
                        .previousText(c1.getContent())
                        .modifiedText(c2.getContent())
                        .changeType(modified ? ChangeType.MODIFIED : ChangeType.UNCHANGED)
                        .build();

                clauseDiffs.add(DiffResponse.ClauseDiffDTO.builder()
                        .clauseId(c2.getId())
                        .clauseNumber(c2.getClauseNumber())
                        .clauseTitle(c2.getTitle())
                        .v1Text(c1.getContent())
                        .v2Text(c2.getContent())
                        .segments(segments)
                        .changeSummary(changeSummary)
                        .build());
            } else if (c1 == null && c2 != null) {
                // Added clause
                changedClauses++;
                List<DiffSegmentDTO> segments = List.of(
                        DiffSegmentDTO.builder().type(ChangeType.ADDED).text(c2.getContent()).build()
                );
                additionsCount += countWords(c2.getContent());

                ClauseChange changeSummary = ClauseChange.builder()
                        .clauseId(c2.getId())
                        .clauseNumber(c2.getClauseNumber())
                        .clauseTitle(c2.getTitle())
                        .previousText("")
                        .modifiedText(c2.getContent())
                        .changeType(ChangeType.ADDED)
                        .build();

                clauseDiffs.add(DiffResponse.ClauseDiffDTO.builder()
                        .clauseId(c2.getId())
                        .clauseNumber(c2.getClauseNumber())
                        .clauseTitle(c2.getTitle())
                        .v1Text("")
                        .v2Text(c2.getContent())
                        .segments(segments)
                        .changeSummary(changeSummary)
                        .build());
            } else if (c1 != null) {
                // Deleted clause
                changedClauses++;
                List<DiffSegmentDTO> segments = List.of(
                        DiffSegmentDTO.builder().type(ChangeType.DELETED).text(c1.getContent()).build()
                );
                deletionsCount += countWords(c1.getContent());

                ClauseChange changeSummary = ClauseChange.builder()
                        .clauseId(c1.getId())
                        .clauseNumber(c1.getClauseNumber())
                        .clauseTitle(c1.getTitle())
                        .previousText(c1.getContent())
                        .modifiedText("")
                        .changeType(ChangeType.DELETED)
                        .build();

                clauseDiffs.add(DiffResponse.ClauseDiffDTO.builder()
                        .clauseId(c1.getId())
                        .clauseNumber(c1.getClauseNumber())
                        .clauseTitle(c1.getTitle())
                        .v1Text(c1.getContent())
                        .v2Text("")
                        .segments(segments)
                        .changeSummary(changeSummary)
                        .build());
            }
        }

        List<DiffSegmentDTO> fullTextDiff = computeWordDiff(
                v1.getFullText() != null ? v1.getFullText() : "",
                v2.getFullText() != null ? v2.getFullText() : ""
        );

        return DiffResponse.builder()
                .contractId(contractId)
                .contractTitle(contractTitle)
                .v1Number(v1.getVersionNumber())
                .v2Number(v2.getVersionNumber())
                .v1(v1)
                .v2(v2)
                .clauseDiffs(clauseDiffs)
                .fullTextDiffSegments(fullTextDiff)
                .totalClausesChanged(changedClauses)
                .additionsCount(additionsCount)
                .deletionsCount(deletionsCount)
                .build();
    }

    public List<DiffSegmentDTO> computeWordDiff(String oldText, String newText) {
        if (oldText == null) oldText = "";
        if (newText == null) newText = "";

        if (oldText.equals(newText)) {
            return List.of(DiffSegmentDTO.builder().type(ChangeType.UNCHANGED).text(oldText).build());
        }

        String[] oldWords = oldText.split("(?<=\\s+)|(?=\\s+)");
        String[] newWords = newText.split("(?<=\\s+)|(?=\\s+)");

        int n = oldWords.length;
        int m = newWords.length;

        // Longest Common Subsequence (LCS) matrix
        int[][] lcs = new int[n + 1][m + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (oldWords[i].equals(newWords[j])) {
                    lcs[i + 1][j + 1] = lcs[i][j] + 1;
                } else {
                    lcs[i + 1][j + 1] = Math.max(lcs[i + 1][j], lcs[i][j + 1]);
                }
            }
        }

        // Backtrack to find diff segments
        List<DiffSegmentDTO> rawSegments = new ArrayList<>();
        int i = n, j = m;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && oldWords[i - 1].equals(newWords[j - 1])) {
                rawSegments.add(DiffSegmentDTO.builder().type(ChangeType.UNCHANGED).text(oldWords[i - 1]).build());
                i--;
                j--;
            } else if (j > 0 && (i == 0 || lcs[i][j - 1] >= lcs[i - 1][j])) {
                rawSegments.add(DiffSegmentDTO.builder().type(ChangeType.ADDED).text(newWords[j - 1]).build());
                j--;
            } else if (i > 0 && (j == 0 || lcs[i][j - 1] < lcs[i - 1][j])) {
                rawSegments.add(DiffSegmentDTO.builder().type(ChangeType.DELETED).text(oldWords[i - 1]).build());
                i--;
            }
        }

        Collections.reverse(rawSegments);

        // Merge contiguous segments of the same ChangeType
        List<DiffSegmentDTO> merged = new ArrayList<>();
        for (DiffSegmentDTO seg : rawSegments) {
            if (merged.isEmpty()) {
                merged.add(new DiffSegmentDTO(seg.getType(), seg.getText()));
            } else {
                DiffSegmentDTO last = merged.get(merged.size() - 1);
                if (last.getType() == seg.getType()) {
                    last.setText(last.getText() + seg.getText());
                } else {
                    merged.add(new DiffSegmentDTO(seg.getType(), seg.getText()));
                }
            }
        }

        return merged;
    }

    private int countWords(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }
}
