package com.intellij.psi.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectAndLibrariesScope extends GlobalSearchScope {
  protected final ProjectFileIndex myProjectFileIndex;

  public ProjectAndLibrariesScope(final Project project) {
    super(project);
    myProjectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
  }

  public boolean contains(VirtualFile file) {
    return myProjectFileIndex.isInContent(file) ||
           myProjectFileIndex.isInLibraryClasses(file) ||
           myProjectFileIndex.isInLibrarySource(file);
  }

  public int compare(VirtualFile file1, VirtualFile file2) {
    List<OrderEntry> entries1 = myProjectFileIndex.getOrderEntriesForFile(file1);
    List<OrderEntry> entries2 = myProjectFileIndex.getOrderEntriesForFile(file2);
    if (entries1.size() != entries2.size()) return 0;

    int res = 0;
    for (OrderEntry entry1 : entries1) {
      Module module = entry1.getOwnerModule();
      ModuleFileIndex moduleFileIndex = ModuleRootManager.getInstance(module).getFileIndex();
      OrderEntry entry2 = moduleFileIndex.getOrderEntryForFile(file2);
      if (entry2 == null) {
        return 0;
      }
      else {
        int aRes = entry2.compareTo(entry1);
        if (aRes == 0) return 0;
        if (res == 0) {
          res = aRes;
        }
        else if (res != aRes) {
          return 0;
        }
      }
    }

    return res;
  }

  public boolean isSearchInModuleContent(@NotNull Module aModule) {
    return true;
  }

  public boolean isSearchInLibraries() {
    return true;
  }

  public String getDisplayName() {
    return PsiBundle.message("psi.search.scope.project.and.libraries");
  }

  @NotNull
  public GlobalSearchScope intersectWith(@NotNull final GlobalSearchScope scope) {
    return scope;
  }

  public GlobalSearchScope uniteWith(@NotNull final GlobalSearchScope scope) {
    return this;
  }

  public String toString() {
    return getDisplayName();
  }
}
