# Changelog
All notable changes about agile service will be documented in this file.

## [0.9.5] - 2018-08-31

### Added

- Dashboard functionality.Users can customize the dashboard in the home page.The dashboard contains: burndown chart, version progress, epic progress, my unfinished issues and more.
- User story map functionality.User story map is based on epic, planning and managing issues according to version and sprint dimensions.
- User story map swim lane functionality.Users can select none, version, sprint to divide the swim lane and record the user's choice of swimlane.
- User story map demand pool functionality.Users can filter all unallocated epic's issues in the demand pool of the user story map.
- User story map issue drag functionality. Users can drag the issue between different epics, versions, sprints, or drag to the map board in the demand pool.
- Report chart caching functionality: Cache the charts in the report via Redis.
- The issues in the chart can transfer to issue management view (without subtasks).
- Unit tests for to-dos, active sprints, release version and component management.
- Record the swim lane settings of the user's active sprint corresponding to the board.
- Issue management view export Excel contains subtasks.

### Changed

- Some view styles are modified.
- Partial view memory optimization.
- Cumulative flow chart query optimization.

### Fixed

- Drag issue repeatedly generates a data log in the to-do view.
- The to-do view has no issue cause sprint data does not receive.
- Dragging issues to an unsupported version failed in the to-do view.
- Dragging issues to an unsupported version failed in the to-do view.
- When there are column constraints of the board configuration in the active sprint view, the constraints can be skipped directly by modifying the state of the to-do view.
- Failed to delete the version in the version management view.

## [0.9.0] - 2018-08-17

### Added

- Version drag sort functionality.Users can drag the version to sort versions in the version management view and the to-do view.
- Epic drag sort functionality.Users can drag the epic to sort epics in the to-do view.
- Quick Search sort functionality.Users can drag the quick search and sort quick searchs in the set-up quick search view.
- Sprint speed chart functionality.Users can select story points, issues counts,remaining time to see issues's resolve and unresolved proportional histogram of different sprints.
- Epic report functionality.Users can select different epics through story points, issues counts, remaining time to see the current sprint resolve, unresolved, unresolved and unpredictable issues.And users also can see the corresponding summary data.
- Issue statistics chart functionality.Users can view the issue statistics chart in the project according to the assignee, component, issue type, fix version, priority, status, sprint, epic and resolve results.
- Issue's details back to history view functionality.Users can back to the original page after clicking on the issue's details in any views.
- Added agile-service unit testing based on Spock.
- Added create branch functionality in the issue's details operation.
- When users modify the state to completed and the issue resolve log will be generated.
- Modify sprint name have length limit.

### Changed

- Agile-service message propagation is modified from Kafka to Saga.
- Optimize the request time of the version report.
- Optimize the equest time of the burndown report.
- View style adjustment for to-do view.
- Optimize the problem of slow loading of epics and versions in the to-do view.
- Data logs processing logic refactoring.
- Modify version status style.

### Fixed
- Memory overflow for to-do view.
- Burndown chart data are inconsistent.
- Cumulative flow chart data are inconsistent.
- Data display is inconsistent after component management creation component.

### Removed
- Details of the issue are displayed on the left side of the narrow work log and activity log.
- Cumulative flow graph statistics for epics and their sub tasks.

## [0.8.0] - 2018-07-20

### Added

- Branch management functionality.Users can creating,merging and viewing branch information from Gitlab remote repository branches associated with issues in issue detail view.
- Version report functionality.The version report shows your team's progress in completing the version. The version report can be filtered based on the remaining estimated time, story points, and issue counts. The version report will also be based on your team's since the release. The average progress (speed) and the estimated remaining workload show you the predicted release date.
- Cumulative flow diagram functionality.The cumulative flow diagram is an area graph showing the status of various work items for the application, version and sprint. The horizontal x-axis represents time, the vertical y-axis represents the problem count. Each colored area of the chart is equivalent to the issue change listed on the panel, and the cumulative flow diagram can be used to identify bottlenecks.If your chart contains areas that are vertically widened over time.A column equal to the widened area usually becomes a bottleneck.
- Test type issue functionality.Test type issue are used in the "Test Manager" module, from which users can create test type issue for managing test cases.
- Project default setting functionality.The project owner can set the default assignee and the default priority of the project's issues. If user create issue havn't assignee or priority.System will create the default assignee and priority according to the project settings. The default priority of the project is lower than the default assignee of the component.
- User default board functionality.If user selects the board,system will recordeing what your select.When the user enter  active sprint view,the board selected by the user will be displayed.
- Issues export Excel functionality.Users can filter out issues according to the selected conditions then export to the table.
- Issue converted to subtask functionality.Users can convert other types of issues into subtasks.In particular, the story issue is converted to a subtask.And the story point is modified to 0.
- Issue copy functionality.Users can copy the issue by selecting parameters:question link and subtask.Copy the issue will generate a copy type link with the original issue.
- The release log can be viewed in the version view.
- Version log export Markdown document functionality.Users can export issues informations to Markdown document in the release version of the version log.

### Changed

- The epic type issues defaults to the initial color modification.
- During update the version association of the issue, you cannot delete the version association that has been archived.
- Optimize the search interface and modify the trigger logic.
- Optimize the burndown chart data query interface.
- The release time display field is modified from the start time to the release time.
- Hidden no lanes are assigned when no issues on the board.
- The issue card on the board can view epic information.
- Modify the active sprint first in the menu order.
- Modify the location of the epic name in the issue details.
- Issue details style optimization.
- To-do list view style optimization.
- Remove the project code duplicate name check in the project settings.
- Issues is arranged to refresh each time in a certain order on the board.
- When you select issues in the to-do list, if you click on the details of one of the issue, you can select multiple issues based on the issue.
- The project creates an initial test type issue.
- Add the action of converted to subtask, copy operation in the issue details.
- Release issues can be linked to issues management by clicking on the link.
- The report view can be associated with a list of issues and details of each issue.
- The report view adds a version report, cumulative flow diagram entry.
- Report selects switches add the version report and the cumulative flowchart.
- The issues in the sprint report can be viewed by grouping in the report to the issues management view.
- The swim lane type in the board settings is added to display issue.

### Fixed
- Issue details Anchor location is not accurate.
- The epic color in the issue details does not match the epic color.
- When the issue is based on the story display and selecting only my issues.The parent task does not belong to the same assignee's swim lane display defect.
- Select the board style question.
- Active sprint story point statistics color error.
- A Easy way to create issues with Caton.
- The filter user limits 20 people when the filter is created.
- The issue details select the manager and reporter component issue.
- You can only select up to 400 issues when selecting a link issue.
- Creationing issue link by search result failed.
- The to-do list issues is dragged into the version but the list of issues in the corresponding version is not refreshed in real time.
- The list shows the data operation load delay.
- The to-do list issues is dragged to the sprint but the sprint's users information is not updated.
- When the issue summary is switched during editing, the contents of the edit box will be cleared.
- The problem is dragged into the version but not create logs.
- New project creation issue but issue number starting from 2.
- Issue Management quickly creates epics but epic havn't epic name.
- The rich text editor failed to break words in the case of multiple English.
- The story issue changed to other types but the issue's story points not set to 0,also when  the epic type issue changed to other types then the issues that belongs to the epic was not updated.
- The board in Firefox browser shows a style error.
- The board's swim lane error  when the epic issues does not exist.