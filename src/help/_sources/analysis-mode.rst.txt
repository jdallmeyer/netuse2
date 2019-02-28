=============
Analysis Mode
=============

Introduction
------------
In Analysis Mode, NetUse2 will retrieve system network information and display it in
the system parameters panel.  If all system information is retrieved successfully,
NetUse2 will attempt to ping http://www.google.com for a period of 60 seconds while
recording whether the ping was successful and the reply time.  NetUse2 will then attempt
to ping the DNS resolver(s) and gateway(s) to determine if those resources are available.
NetUse2 will display a line graph containing round-trip times (RTTs) over the 60 second
interval for user analysis.  NetUse2 also displays a pie chart containing the ratio of
successful ping attempts, bad ping attempts, and high-latency (>300ms) ping attempts.

Procedure
---------

1.  Launch the NetUse2 application
2.  In the NetUse2 application, ensure that the **Analysis** tab is selected in the Mode panel

.. image:: auto1.png

3.  Inspect the **System Parameters** panel to verify system network parameters
4.  In the **Analysis Tab**, click on the **Go** button to begin automatic analysis

.. image:: auto2.png

5.  Observe the reply time graph and reply statistics chart fill with data and the status log
    populate with process status

    **NOTE:** The **Stop** button is now active to abort the command

.. image:: auto3.png

6.  When the analysis is complete, inspect the **Issues Detected** panel to see if any
    common network problems have been detected.

    **NOTE:** The **Clear** button is now active to reset NetUse2 for another use

.. image:: auto4.png

7.  If you wish to perform another command, click the **Clear** button to reset NetUse2.
    If you would like to export the data, refer to the :doc:`Data <data>` section to learn
    how to import and export ping data.

