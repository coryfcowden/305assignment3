package org.example;

import javiergs.tulip.GitHubHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all GitHub-related operations for downloading Java source files.
 *
 * This class interacts with the provided GitHubHandler to:
 * - Recursively list all files in a GitHub folder URL
 * - Filter only Java files
 * - Convert tree URLs into blob URLs
 * - Retrieve the raw file contents of each Java file
 *
 * The Nanny acts as a helper/facade for simplifying GitHub access so the rest
 * of the application does not need to deal with URL formats or API logic.
 * @Author Cory Cowden
 */


public class Nanny {

    private final GitHubHandler gh;

    public static class FileEntry {
        public final String path;
        public final String downloadUrl;

        public FileEntry(String path, String downloadUrl) {
            this.path = path;
            this.downloadUrl = downloadUrl;
        }
    }

    public Nanny(String token) {
        this.gh = new GitHubHandler(token);
    }

    public List<FileEntry> listFilesRecursiveFromGitHubFolderUrl(String githubFolderUrl) throws IOException {

        List<String> paths = gh.listFilesRecursive(githubFolderUrl);

        List<FileEntry> out = new ArrayList<>();

        for (String path : paths) {
            if (!path.endsWith(".java")) continue;

            // convert from /tree/ URL → /blob/ URL
            String downloadUrl = githubFolderUrl.replace("/tree/", "/blob/") + "/" + path;

            out.add(new FileEntry(path, downloadUrl));
        }

        return out;
    }

    public String getFileContentFromDownloadUrl(String downloadUrl) throws IOException {
        return gh.getFileContentFromUrl(downloadUrl);
    }
}
