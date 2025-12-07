package org.plugin;

import org.apache.maven.model.Developer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

/**
 * Mojo-класс для вывода информации о разработчиках из pom.xml.
 */
@Mojo(name = "info")
public class DeveloperInfoMojo extends AbstractMojo {

    /**
     * Инъекция текущего Maven-проекта.
     * Maven автоматически подставит сюда объект текущего проекта.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("=======================================");
        getLog().info("      Информация о разработчиках       ");
        getLog().info("=======================================");

        List<Developer> developers = project.getDevelopers();

        if (developers == null || developers.isEmpty()) {
            getLog().info("В файле pom.xml не найдена информация о разработчиках.");
            return;
        }

        for (int i = 0; i < developers.size(); i++) {
            Developer dev = developers.get(i);
            getLog().info("Разработчик #" + (i + 1));
            getLog().info("  ID: " + dev.getId());
            getLog().info("  Имя: " + dev.getName());
            getLog().info("  Email: " + dev.getEmail());
            getLog().info("  Организация: " + dev.getOrganization());
            if (dev.getRoles() != null && !dev.getRoles().isEmpty()) {
                getLog().info("  Роли: " + String.join(", ", dev.getRoles()));
            }
            getLog().info("---------------------------------------");
        }
    }
}
